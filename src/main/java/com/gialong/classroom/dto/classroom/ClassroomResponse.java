package com.gialong.classroom.dto.classroom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassroomResponse {
    private Long id;
    private String name;
    private String description;
    private String joinCode;
    private String createdBy;
}