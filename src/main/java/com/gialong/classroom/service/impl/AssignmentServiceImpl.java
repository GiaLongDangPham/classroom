package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.assignment.AssignmentRequest;
import com.gialong.classroom.dto.assignment.AssignmentResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Assignment;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.AssignmentRepository;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final ClassroomRepository classroomRepository;
    private final AuthServiceImpl authServiceImpl;

    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        User user = authServiceImpl.getCurrentUser();
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Assignment assignment = Assignment.builder()
                .classroom(classroom)
                .createdBy(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .fileUrl(request.getFileUrl())
                .build();

        assignmentRepository.save(assignment);

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .fileUrl(assignment.getFileUrl())
                .createdBy(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .build();
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByClassroom(Long classroomId) {
        return assignmentRepository.findByClassroomId(classroomId)
                .stream()
                .map(a -> AssignmentResponse.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .dueDate(a.getDueDate())
                        .fileUrl(a.getFileUrl())
                        .createdBy(String.format("%s %s", a.getCreatedBy().getFirstName(), a.getCreatedBy().getLastName()))
                        .build())
                .toList();
    }
}