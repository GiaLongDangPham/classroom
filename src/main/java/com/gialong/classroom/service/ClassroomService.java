package com.gialong.classroom.service;

import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.dto.classroom.MemberResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.*;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthService authService;
    private final ChatMessageRepository chatMessageRepository;

    public ClassroomResponse createClass(ClassroomRequest request) {
        // Lấy người dùng hiện tại từ SecurityContext
        User currentUser = authService.getCurrentUser();

        // Chỉ cho phép TEACHER
        if (currentUser.getRole() != Role.TEACHER) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        // Tạo mã lớp ngẫu nhiên
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

        return toClassroomResponse(classroom);
    }

    public ClassroomResponse joinClass(String joinCode) {
        User currentUser = authService.getCurrentUser();

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

    public List<ClassroomResponse> getMyClasses() {
        User currentUser = authService.getCurrentUser();

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(currentUser.getId());

        return enrollments.stream()
                .map(enrollment -> {
                    Classroom classroom = enrollment.getClassroom();
                    return toClassroomResponse(classroom);
                })
                .toList();
    }

    public ClassroomResponse getClassDetail(Long classId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        return toClassroomResponse(classroom);
    }

    public void deleteClass(Long classId) {
        User currentUser = authService.getCurrentUser();

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

    public void leaveClass(Long classId) {
        User currentUser = authService.getCurrentUser();

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

    public List<ClassroomResponse> getExploreClasses() {
        User currentUser = authService.getCurrentUser();
        List<Long> joinedClassIds = enrollmentRepository.findByUserId(currentUser.getId())
                .stream().map(e -> e.getClassroom().getId()).toList();

        List<Classroom> all = classroomRepository.findAll();
        return all.stream()
                .filter(c -> !joinedClassIds.contains(c.getId()))
                .map(this::toClassroomResponse)
                .toList();
    }


    private List<MemberResponse> getAllMembersInClassroom(Long classroomId) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassroomId(classroomId);

        return enrollments.stream().map(enrollment -> MemberResponse.builder()
                .id(enrollment.getUser().getId())
                .fullName(String.format("%s %s", enrollment.getUser().getFirstName(), enrollment.getUser().getLastName()))
                .email(enrollment.getUser().getEmail())
                .avatarUrl(enrollment.getUser().getAvatarUrl())
                .roleInClass(enrollment.getRoleInClass().name())
                .build()).toList();
    }

    private String generateJoinCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
                .lastMessage(lastMessage.isPresent() ? lastMessage.get().getContent() : null)
                .build();
    }

}