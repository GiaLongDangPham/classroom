package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.auth.AuthRequest;
import com.gialong.classroom.dto.auth.AuthResponse;
import com.gialong.classroom.dto.auth.LogoutRequest;
import com.gialong.classroom.dto.auth.RefreshTokenRequest;
import com.gialong.classroom.dto.user.UserResponse;
import com.gialong.classroom.model.User;
import com.gialong.classroom.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseData<?> register(@RequestBody @Valid AuthRequest request) {
        User user = authService.register(request);
        return ResponseData.builder()
                .code(HttpStatus.CREATED.value())
                .message("Register success")
                .data(user)
                .build();
    }

    @PostMapping("/login")
    public ResponseData<?> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("Login success")
                .data(authResponse)
                .build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        AuthResponse authResponse = authService.refresh(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/logout")
    ResponseData<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.signOut(request);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sign out success")
                .build();
    }

    @GetMapping("/me")
    public ResponseData<?> getProfile() {
        User currentUser = authService.getCurrentUser();
        UserResponse userResponse = authService.toUserResponse(currentUser);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("User info fetched successfully")
                .data(userResponse)
                .build();
    }
}