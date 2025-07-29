package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.postlike.PostLikeResponse;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikeService {
    @Transactional
    PostLikeResponse likeOrUnlikePost(Long postId);

    boolean isLiked(Long postId, Long userId);

    Long countLikes(Long postId);
}
