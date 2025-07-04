package com.gialong.classroom.dto.submission;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionResponse {
    private Long id;
    private String studentName;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Integer score;
}