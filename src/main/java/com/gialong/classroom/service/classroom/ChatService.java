package com.gialong.classroom.service.classroom;


import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessage;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthService authService;

    public List<ChatMessageResponse> getMessagesByClassroomId(Long classroomId) {

        return chatMessageRepository.findByClassroomIdOrderBySentAtAsc(classroomId)
                .stream()
                .map(this::toChatMessageResponse)
                .toList();
    }

    public ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .classroomId(chatMessage.getClassroom().getId())
                .content(chatMessage.getContent())
                .user(authService.toUserResponse(chatMessage.getSender()))
                .sentAt(chatMessage.getSentAt())
                .build();
    }
}