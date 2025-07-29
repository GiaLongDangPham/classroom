package com.gialong.classroom.controller.chat;

import com.gialong.classroom.dto.chat.ChatMessageRequest;
import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessage;
import com.gialong.classroom.model.ChatMessageElasticSearch;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.repository.UserRepository;
import com.gialong.classroom.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/chat.sendMessage/{classroomId}")
    public void sendMessage(@DestinationVariable Long classroomId, ChatMessageRequest message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                                                            .content(message.getContent())
                                                            .sender(sender)
                                                            .classroom(Classroom.builder().id(classroomId).build())
                                                            .sentAt(LocalDateTime.now())
                                                            .build());
        // Sau khi lưu vào db, lưu vào elastic
        ChatMessageElasticSearch elasticMessage = ChatMessageElasticSearch.builder()
                .id(String.valueOf(saved.getId()))
                .content(saved.getContent())
                .classroomId(String.valueOf(saved.getClassroom().getId()))
                .senderName(saved.getSender().getFirstName() + " " + saved.getSender().getLastName())
                .build();
//        chatMessageElasticRepository.save(elasticMessage);

        kafkaTemplate.send("save-to-elastic-search", elasticMessage);

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