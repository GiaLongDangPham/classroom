package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.assignment.AssignmentRequest;
import com.gialong.classroom.dto.assignment.AssignmentResponse;
import com.gialong.classroom.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseData<?> create(@RequestBody AssignmentRequest request) {
        AssignmentResponse response = assignmentService.createAssignment(request);
        return ResponseData.builder()
                .code(HttpStatus.CREATED.value())
                .message("create success")
                .data(response)
                .build();
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseData<?> getByClassroom(@PathVariable Long classroomId) {
        List<AssignmentResponse> response = assignmentService.getAssignmentsByClassroom(classroomId);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("get assignment successfully")
                .data(response)
                .build();
    }
}