package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.auth.AuthRequest;
import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.model.User;
import com.gialong.classroom.service.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping("/create")
    public ResponseData<?> createClass(@RequestBody @Valid ClassroomRequest request) {
        ClassroomResponse classroomResponse = classroomService.createClass(request);
        return ResponseData.builder()
                .code(HttpStatus.CREATED.value())
                .message("create class successfully")
                .data(classroomResponse)
                .build();
    }

    @PostMapping("/join")
    public ResponseData<?> joinClass(@RequestParam String joinCode) {
        ClassroomResponse classroomResponse = classroomService.joinClass(joinCode);
        return ResponseData.builder()
                .code(HttpStatus.CREATED.value())
                .message("join class successfully")
                .data(classroomResponse)
                .build();
    }
}
