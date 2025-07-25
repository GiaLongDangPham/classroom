package com.gialong.classroom.dto.post.postlike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
}
