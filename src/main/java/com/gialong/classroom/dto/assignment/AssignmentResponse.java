package com.gialong.classroom.dto.assignment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String fileUrl;
    private String createdBy;
}