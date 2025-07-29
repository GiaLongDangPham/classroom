package com.gialong.classroom.service;

import com.gialong.classroom.model.ChatMessageElasticSearch;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaService {
    @KafkaListener(topics = "save-to-elastic-search", groupId = "message-elastic-search")
    void saveMessageToElasticSearch(ChatMessageElasticSearch message, Acknowledgment acknowledgment);
}
