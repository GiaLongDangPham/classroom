package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.postcomment.PostCommentResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.PostComment;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.PostCommentRepository;
import com.gialong.classroom.repository.PostRepository;
import com.gialong.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public List<PostCommentResponse> getComments(Long postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toPostCommentResponse)
                .toList();
    }

    public PostCommentResponse addComment(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = authService.getCurrentUser();

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return toPostCommentResponse(postCommentRepository.save(comment));
    }

    private PostCommentResponse toPostCommentResponse(PostComment postComment) {
        User user = postComment.getUser();
        return PostCommentResponse.builder()
                .id(postComment.getId())
                .content(postComment.getContent())
                .postId(postComment.getPost().getId())
                .userId(postComment.getUser().getId())
                .createdAt(postComment.getCreatedAt())
                .createdBy(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .build();
    }
}