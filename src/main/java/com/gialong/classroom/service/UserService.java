package com.gialong.classroom.service;

import com.gialong.classroom.dto.user.ChangePasswordRequest;
import com.gialong.classroom.dto.user.UpdateProfileRequest;
import com.gialong.classroom.model.User;

public interface UserService {
    User updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void updateAvatar(String avatarUrl);
}
