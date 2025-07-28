package com.gialong.classroom.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Document(indexName = "classroom")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClassroomElasticSearch implements Serializable {
    @Serial
    private static final long serialVersionUID = -5257626960164837310L;

    @Id
    private String id;

    @Field(name = "name", type = FieldType.Text)
    private String name;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "joinCode", type = FieldType.Text)
    private String joinCode;

    @Field(name = "created_by", type = FieldType.Text)
    private String createdBy;
}
