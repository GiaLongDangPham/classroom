package com.gialong.classroom.dto.classroom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassroomResponse {
    private Long id;
    private String name;
    private String description;
    private String joinCode;
    private String createdBy;
    private List<MemberResponse> members;

    private String lastMessage;
    private LocalDateTime lastMessageTimestamp;
}