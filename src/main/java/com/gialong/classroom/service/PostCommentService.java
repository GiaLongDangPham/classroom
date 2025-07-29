package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.postcomment.PostCommentResponse;

import java.util.List;

public interface PostCommentService {
    List<PostCommentResponse> getComments(Long postId);

    PostCommentResponse addComment(Long postId, String content);

    Long countComments(Long postId);

    void deleteComment(Long commentId);

    PostCommentResponse updateComment(Long commentId, String content);
}
