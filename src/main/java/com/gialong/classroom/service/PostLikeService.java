package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.postlike.PostLikeResponse;

public interface PostLikeService {
    PostLikeResponse likeOrUnlikePost(Long postId);

    boolean isLiked(Long postId, Long userId);

    Long countLikes(Long postId);
}
