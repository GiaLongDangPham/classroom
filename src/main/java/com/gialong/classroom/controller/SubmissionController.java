package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.submission.SubmissionRequest;
import com.gialong.classroom.dto.submission.SubmissionResponse;
import com.gialong.classroom.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping
    public ResponseData<?> submit(@RequestBody SubmissionRequest request) {
        SubmissionResponse submissionResponse =  submissionService.submitAssignment(request);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("submit successfully")
                .data(submissionResponse)
                .build();
    }

    @PutMapping("/{id}/grade")
    public ResponseData<?> grade(@PathVariable Long id, @RequestParam int score) {
        submissionService.gradeSubmission(id, score);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("update grade successfully")
                .build();
    }
}
