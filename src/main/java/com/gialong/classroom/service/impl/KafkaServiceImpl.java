package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.email.NotificationEvent;
import com.gialong.classroom.dto.email.Recipient;
import com.gialong.classroom.dto.email.SendEmailRequest;
import com.gialong.classroom.model.ChatMessageElasticSearch;
import com.gialong.classroom.repository.ChatMessageElasticRepository;
import com.gialong.classroom.service.EmailService;
import com.gialong.classroom.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "KAFKA-SERVICE")
public class KafkaServiceImpl implements KafkaService {

    private final ChatMessageElasticRepository chatMessageElasticRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "save-to-elastic-search", groupId = "message-elastic-search")
    @Override
    public void saveMessageToElasticSearch(ChatMessageElasticSearch message, Acknowledgment acknowledgment) {
        try {
            log.info("Start saving message to elasticsearch ");
            if(message != null && !chatMessageElasticRepository.existsById(message.getId())) {
                chatMessageElasticRepository.save(message);
                log.info("saved message to elasticsearch success ");
                // Commit offset sau khi xử lý thành công
                acknowledgment.acknowledge();
            } else {
                log.error("saving message to elasticsearch failed");
            }
        }catch (Exception e) {
            log.error("Error while saving message to Elasticsearch: {}", e.getMessage());
            throw e;
        }
    }

    @KafkaListener(topics = "send-email", groupId = "notification-group")
    @Override
    public void sendEmail(NotificationEvent message) {
        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder()
                        .email(message.getRecipient())
                        .build())
                .subject(message.getSubject())
                .htmlContent(message.getBody())
                .build());
    }
}
