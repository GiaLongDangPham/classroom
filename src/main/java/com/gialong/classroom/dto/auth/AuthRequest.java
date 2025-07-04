package com.gialong.classroom.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String firstName;

    private String lastName;

    @Email(message = "Email is not valid")
    private String email;

    private String role; // Optional, sẽ parse sau
}