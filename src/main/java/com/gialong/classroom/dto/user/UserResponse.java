package com.gialong.classroom.dto.user;

import com.gialong.classroom.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatarUrl;

    private LocalDateTime createdAt;

    private Role role;
}
