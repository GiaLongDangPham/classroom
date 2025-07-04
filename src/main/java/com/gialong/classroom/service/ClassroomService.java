package com.gialong.classroom.service;

import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.Enrollment;
import com.gialong.classroom.model.Role;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthService authService;

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

        return new ClassroomResponse(
                classroom.getId(),
                classroom.getName(),
                classroom.getDescription(),
                classroom.getJoinCode(),
                String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName())
        );
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

        return new ClassroomResponse(
                classroom.getId(),
                classroom.getName(),
                classroom.getDescription(),
                classroom.getJoinCode(),
                String.format("%s %s", currentUser.getFirstName(), currentUser.getLastName())
        );
    }


    private String generateJoinCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}