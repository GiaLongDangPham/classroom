package com.gialong.classroom.service;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessageElasticSearch;

import java.util.List;

public interface ChatService {
    PageResponse<ChatMessageElasticSearch> searchMessages(
            int page, int size, String keywordContent, String keywordSender, String classroomId
    );

    List<ChatMessageResponse> getMessagesByClassroomId(Long classroomId);
}
