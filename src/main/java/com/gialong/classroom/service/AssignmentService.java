package com.gialong.classroom.service;

import com.gialong.classroom.dto.assignment.AssignmentRequest;
import com.gialong.classroom.dto.assignment.AssignmentResponse;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);

    List<AssignmentResponse> getAssignmentsByClassroom(Long classroomId);
}
