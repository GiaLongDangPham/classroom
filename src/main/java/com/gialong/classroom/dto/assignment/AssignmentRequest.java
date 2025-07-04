package com.gialong.classroom.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    private Long classroomId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String fileUrl;
}