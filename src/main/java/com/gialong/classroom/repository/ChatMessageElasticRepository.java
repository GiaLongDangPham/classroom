package com.gialong.classroom.repository;

import com.gialong.classroom.model.ChatMessageElasticSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageElasticRepository extends ElasticsearchRepository<ChatMessageElasticSearch, String> {
}