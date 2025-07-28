package com.gialong.classroom.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.gialong.classroom.model.ClassroomElasticSearch;

import java.util.List;

@Repository
public interface ClassroomElasticRepository extends ElasticsearchRepository<ClassroomElasticSearch, String> {
    @Query("""
    {
      "multi_match": {
        "query": "?0",
        "fields": ["name^2", "description"],
        "fuzziness": "AUTO"
      }
    }
    """)
    List<ClassroomElasticSearch> searchByKeyword(String keyword);
}
