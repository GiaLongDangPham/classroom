package com.gialong.classroom.service;

import com.gialong.classroom.dto.auth.AuthRequest;
import com.gialong.classroom.dto.auth.AuthResponse;
import com.gialong.classroom.dto.auth.LogoutRequest;
import com.gialong.classroom.dto.user.UserResponse;
import com.gialong.classroom.model.User;
import jakarta.validation.Valid;

public interface AuthService {
    User register(AuthRequest request);

    AuthResponse login(AuthRequest request);

    AuthResponse refresh(String refreshToken);

    void signOut(@Valid LogoutRequest request);

    User getCurrentUser();

    UserResponse toUserResponse(User user);

    AuthResponse outboundAuthenticate(String code);
}
