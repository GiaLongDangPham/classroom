package com.gialong.classroom.controller.chat;

import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{classroomId}")
    public ResponseEntity<?> getChatMessages(@PathVariable Long classroomId) {
        List<ChatMessageResponse> messages = chatService.getMessagesByClassroomId(classroomId);
        return ResponseEntity.ok(messages);
    }
}