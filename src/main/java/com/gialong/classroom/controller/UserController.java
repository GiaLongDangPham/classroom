package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.user.ChangePasswordRequest;
import com.gialong.classroom.dto.user.UpdateProfileRequest;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.UserRepository;
import com.gialong.classroom.service.AuthService;
import com.gialong.classroom.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final String UPLOAD_DIR = "uploads/avatars/";

    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @PutMapping("/update/{id}")
    public ResponseData<?> updateProfile(@PathVariable Long id,
                                           @RequestBody @Valid UpdateProfileRequest request) {
        User user = userService.updateProfile(id, request);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("Profile updated successfully")
                .data(user)
                .build();
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id,
                                            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }


}
