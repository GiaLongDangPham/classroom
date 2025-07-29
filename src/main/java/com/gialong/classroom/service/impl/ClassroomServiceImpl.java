package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.dto.classroom.MemberResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.*;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.repository.ClassroomElasticRepository;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.repository.EnrollmentRepository;
import com.gialong.classroom.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthServiceImpl authServiceImpl;
    private final ChatMessageRepository chatMessageRepository;
    private final ClassroomElasticRepository classroomElasticRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public ClassroomResponse createClass(ClassroomRequest request) {
        User currentUser = authServiceImpl.getCurrentUser();

        if (currentUser.getRole() != Role.TEACHER) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        String joinCode = generateJoinCode();

        // Tạo classroom
        Classroom classroom = Classroom.builder()
                .name(request.getName())
                .description(request.getDescription())
                .joinCode(joinCode)
                .createdBy(currentUser)
                .build();

        classroomRepository.save(classroom);

        // Thêm người tạo lớp vào enrollment với vai trò TEACHER
        Enrollment enrollment = Enrollment.builder()
                .classroom(classroom)
                .user(currentUser)
                .roleInClass(Role.TEACHER)
                .build();
        enrollmentRepository.save(enrollment);

        // Sau khi lưu MySQL -> lưu vào Elasticsearch
        ClassroomElasticSearch elastic = ClassroomElasticSearch.builder()
                .id(classroom.getId().toString())
                .name(classroom.getName())
                .description(classroom.getDescription())
                .joinCode(classroom.getJoinCode())
                .createdBy(classroom.getCreatedBy().getFirstName() + " " + classroom.getCreatedBy().getLastName())
                .build();
        classroomElasticRepository.save(elastic);

        return toClassroomResponse(classroom);
    }

    @Override
    public PageResponse<ClassroomElasticSearch> searchElastic(int page, int size, String keyword) {
        NativeQuery query;

        if (keyword == null || keyword.trim().isEmpty()) {
            query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(PageRequest.of(page - 1, size))
                    .build();
        } else {
            query = NativeQuery.builder()
                    .withQuery(q -> q.bool(b -> b
                            .should(s -> s.match(m -> m.field("name")
                                    .query(keyword)
                                    .fuzziness("AUTO")
                                    .minimumShouldMatch("70%")
                                    .boost(2.0F)))
                            .should(s -> s.match(m -> m.field("description")
                                    .query(keyword)
                                    .fuzziness("AUTO")
                                    .minimumShouldMatch("70%")))
                            .should(s -> s.matchPhrasePrefix(m -> m.field("joinCode")
                                    .query(keyword)))
                            .should(s -> s.match(m -> m.field("createdBy")
                                    .query(keyword)))
                    ))
                    .withPageable(PageRequest.of(page - 1, size))
                    .build();
        }

        SearchHits<ClassroomElasticSearch> hits = elasticsearchTemplate.search(query, ClassroomElasticSearch.class);
        long total = hits.getTotalHits();

        return PageResponse.<ClassroomElasticSearch>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .data(hits.getSearchHits().stream().map(SearchHit::getContent).toList())
                .build();
    }

    @Override
    public ClassroomResponse joinClass(String joinCode) {
        User currentUser = authServiceImpl.getCurrentUser();

        // Tìm lớp học theo mã
        Classroom classroom = classroomRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // Kiểm tra đã tham gia chưa
        boolean alreadyEnrolled = enrollmentRepository
                .existsByUserIdAndClassroomId(currentUser.getId(), classroom.getId());
        if (alreadyEnrolled) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED_CLASS);
        }

        // Tạo bản ghi Enrollment với vai trò STUDENT
        Enrollment enrollment = Enrollment.builder()
                .user(currentUser)
                .classroom(classroom)
                .roleInClass(Role.STUDENT)
                .build();
        enrollmentRepository.save(enrollment);

        return toClassroomResponse(classroom);
    }

    @Override
    public List<ClassroomResponse> getMyClasses() {
        User currentUser = authServiceImpl.getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(currentUser.getId());

        return enrollments.stream()
                .map(enrollment -> {
                    Classroom classroom = enrollment.getClassroom();
                    return toClassroomResponse(classroom);
                })
                .toList();
    }

    @Override
    public ClassroomResponse getClassDetail(Long classId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        return toClassroomResponse(classroom);
    }

    @Override
    public void deleteClass(Long classId) {
        User currentUser = authServiceImpl.getCurrentUser();

        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        if (!classroom.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        // Xoá hết enrollments trước
        enrollmentRepository.deleteAll(enrollmentRepository.findByClassroomId(classId));

        // Xoá lớp
        classroomRepository.delete(classroom);
    }

    @Override
    public void leaveClass(Long classId) {
        User currentUser = authServiceImpl.getCurrentUser();

        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndClassroomId(
                        currentUser.getId(), classId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_ENROLLED_CLASS));

        // Nếu người rời là creator => Xoá lớp luôn
        if (classroom.getCreatedBy().getId().equals(currentUser.getId())) {
            deleteClass(classId);
            return;
        }

        // Nếu không phải creator => chỉ xoá enrollment
        enrollmentRepository.delete(enrollment);

        // Nếu không còn ai trong lớp => tự xoá lớp
        if (enrollmentRepository.findByClassroomId(classId).isEmpty()) {
            classroomRepository.delete(classroom);
        }
    }

    @Override
    public List<ClassroomResponse> getExploreClasses() {
        User currentUser = authServiceImpl.getCurrentUser();
        List<Long> joinedClassIds = enrollmentRepository.findByUserId(currentUser.getId())
                .stream().map(e -> e.getClassroom().getId()).toList();

        List<Classroom> all = classroomRepository.findAll();
        return all.stream()
                .filter(c -> !joinedClassIds.contains(c.getId()))
                .map(this::toClassroomResponse)
                .toList();
    }


    private ClassroomResponse toClassroomResponse(Classroom classroom) {
        Optional<ChatMessage> lastMessage = chatMessageRepository.findTopByClassroomIdOrderBySentAtDesc(classroom.getId());

        return ClassroomResponse.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .description(classroom.getDescription())
                .joinCode(classroom.getJoinCode())
                .createdBy(String.format("%s %s",
                        classroom.getCreatedBy().getFirstName(),
                        classroom.getCreatedBy().getLastName()))
                .members(getAllMembersInClassroom(classroom.getId()))
                .lastMessage(lastMessage.map(ChatMessage::getContent).orElse(null))
                .lastMessageTimestamp(lastMessage.map(ChatMessage::getSentAt).orElse(null))
                .build();
    }

}