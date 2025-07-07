package com.gialong.classroom.controller.chat;

import com.gialong.classroom.dto.chat.ChatMessageRequest;
import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessage;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.repository.UserRepository;
import com.gialong.classroom.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage/{classroomId}")
    public void sendMessage(@DestinationVariable Long classroomId, ChatMessageRequest message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                                                            .content(message.getContent())
                                                            .sender(sender)
                                                            .classroom(Classroom.builder().id(classroomId).build())
                                                            .sentAt(LocalDateTime.now())
                                                            .build());

        messagingTemplate.convertAndSend(
                "/topic/classroom/" + classroomId,
                ChatMessageResponse.builder()
                        .id(saved.getId())
                        .classroomId(saved.getClassroom().getId())
                        .content(saved.getContent())
                        .user(authService.toUserResponse(saved.getSender()))
                        .sentAt(saved.getSentAt())
                        .build()
        );
    }
}