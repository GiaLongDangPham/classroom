package com.gialong.classroom.dto.chat;

import com.gialong.classroom.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long classroomId;
    private String content;
    private UserResponse user;
    private LocalDateTime sentAt;
}