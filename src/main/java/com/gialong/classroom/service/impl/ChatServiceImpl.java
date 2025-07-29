package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.chat.ChatMessageResponse;
import com.gialong.classroom.model.ChatMessage;
import com.gialong.classroom.model.ChatMessageElasticSearch;
import com.gialong.classroom.repository.ChatMessageElasticRepository;
import com.gialong.classroom.repository.ChatMessageRepository;
import com.gialong.classroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthServiceImpl authServiceImpl;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ChatMessageElasticRepository chatMessageElasticRepository;


    @Override
    public PageResponse<ChatMessageElasticSearch> searchMessages(
            int page, int size, String keywordContent, String keywordSender, String classroomId
    ) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    // Luôn lọc theo classroomId
                    b.must(m -> m.term(t -> t
                            .field("classroomId")
                            .value(classroomId)
                    ));
                    long countFieldsToSearch = 0;
                    //Nếu có search theo content
                    if (keywordContent != null && !keywordContent.trim().isEmpty()) {
                        countFieldsToSearch++;
                        b.should(s -> s.match(m -> m
                                .field("content")
                                .query(keywordContent)
                                .fuzziness("AUTO")
                                .minimumShouldMatch("70%")
                                .boost(2.0f)
                        ));
                    }
                    //Nếu có search theo sender
                    if (keywordSender != null && !keywordSender.trim().isEmpty()) {
                        countFieldsToSearch++;
                        b.should(s -> s.match(m -> m
                                .field("senderName")
                                .query(keywordSender)
                                .fuzziness("AUTO")
                                .minimumShouldMatch("70%")
                        ));
                    }

                    if (countFieldsToSearch != 0) {
                        b.minimumShouldMatch(String.valueOf(countFieldsToSearch));
                    }

                    return b;
                }))
                .withPageable(PageRequest.of(page - 1, size))
                .build();

        SearchHits<ChatMessageElasticSearch> searchHits = elasticsearchTemplate.search(query, ChatMessageElasticSearch.class);
        long total = searchHits.getTotalHits();

        return PageResponse.<ChatMessageElasticSearch>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .data(searchHits.getSearchHits().stream().map(SearchHit::getContent).toList())
                .build();
    }

    @Override
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
                .user(authServiceImpl.toUserResponse(chatMessage.getSender()))
                .sentAt(chatMessage.getSentAt())
                .build();
    }

    @Transactional
    protected void migrateAllMessagesToElastic() {
        List<ChatMessage> allMessages = chatMessageRepository.findAll();

        List<ChatMessageElasticSearch> elasticMessages = allMessages.stream()
                .map(message -> ChatMessageElasticSearch.builder()
                        .id(String.valueOf(message.getId()))
                        .content(message.getContent())
                        .classroomId(String.valueOf(message.getClassroom().getId()))
                        .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                        .build()
                ).toList();

        chatMessageElasticRepository.saveAll(elasticMessages);
    }
}