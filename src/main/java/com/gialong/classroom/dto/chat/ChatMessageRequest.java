package com.gialong.classroom.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageRequest {
    private Long classroomId;
    private String content;
    private Long senderId;
}