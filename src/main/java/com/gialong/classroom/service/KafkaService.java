package com.gialong.classroom.service;

import com.gialong.classroom.model.ChatMessageElasticSearch;
import com.gialong.classroom.repository.ChatMessageElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "KAFKA-SERVICE")
public class KafkaService {

    private final ChatMessageElasticRepository chatMessageElasticRepository;

    @KafkaListener(topics = "save-to-elastic-search", groupId = "message-elastic-search")
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
            throw e; // ném ngoại lệ để kafka tự retry
        }
    }
}
