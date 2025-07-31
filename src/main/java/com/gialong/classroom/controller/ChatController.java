package com.gialong.classroom.controller;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessageElasticSearch;
import com.gialong.classroom.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{classroomId}")
    public ResponseEntity<?> getChatMessages(@PathVariable Long classroomId) {
        List<ChatMessageResponse> messages = chatService.getMessagesByClassroomId(classroomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/search-message-with-elasticsearch")
    public ResponseData<PageResponse<ChatMessageElasticSearch>> searchMessages(
            HttpServletRequest request,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "keywordContent", required = false) String keywordContent,
            @RequestParam(name = "keywordSender", required = false) String keywordSender,
            @RequestParam(name = "classroomId") String classroomId
    ) {
        var result = chatService.searchMessages(page, size, keywordContent, keywordSender, classroomId);
        return ResponseData.<PageResponse<ChatMessageElasticSearch>>builder()
                .code(HttpStatus.OK.value())
                .message("Get classrooms with elastic search")
                .data(result)
                .build();
    }

    @PostMapping("/migrate-all-messages-to-elastic")
    public void migrateAllMessagesToElastic() {
        chatService.migrateAllMessagesToElastic();
    }
}