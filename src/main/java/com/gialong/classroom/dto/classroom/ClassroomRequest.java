package com.gialong.classroom.dto.classroom;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassroomRequest {
    @NotBlank
    private String name;
    private String description;
}