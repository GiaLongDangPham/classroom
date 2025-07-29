package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.post.postcomment.PostCommentResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.PostComment;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.PostCommentRepository;
import com.gialong.classroom.repository.PostRepository;
import com.gialong.classroom.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final AuthServiceImpl authServiceImpl;

    @Override
    public List<PostCommentResponse> getComments(Long postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::toPostCommentResponse)
                .toList();
    }

    @Override
    public PostCommentResponse addComment(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = authServiceImpl.getCurrentUser();

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return toPostCommentResponse(postCommentRepository.save(comment));
    }

    @Override
    public Long countComments(Long postId) {
        return postCommentRepository.countByPostId(postId);
    }

    @Override
    public void deleteComment(Long commentId) {
        postCommentRepository.deleteById(commentId);
    }

    @Override
    public PostCommentResponse updateComment(Long commentId, String content) {
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        postComment.setContent(content);
        postComment.setCreatedAt(LocalDateTime.now());
        return toPostCommentResponse(postCommentRepository.save(postComment));
    }

    private PostCommentResponse toPostCommentResponse(PostComment postComment) {
        User user = postComment.getUser();
        return PostCommentResponse.builder()
                .id(postComment.getId())
                .content(postComment.getContent())
                .postId(postComment.getPost().getId())
                .userId(user.getId())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(postComment.getCreatedAt())
                .createdBy(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .build();
    }
}
