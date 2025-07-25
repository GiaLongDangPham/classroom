package com.gialong.classroom.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String avatarUrl; //Avatar của user đã tạo
    private LocalDateTime createdAt;
    private String createdBy;
    private Long classroomId;
}