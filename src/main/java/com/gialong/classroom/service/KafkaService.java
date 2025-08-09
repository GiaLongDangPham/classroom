package com.gialong.classroom.service;

import com.gialong.classroom.dto.email.NotificationEvent;
import com.gialong.classroom.model.ChatMessageElasticSearch;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaService {
    void saveMessageToElasticSearch(ChatMessageElasticSearch message, Acknowledgment acknowledgment);
    void sendEmail(NotificationEvent message);
}
