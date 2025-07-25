package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.postlike.PostLikeResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.PostLike;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.PostLikeRepository;
import com.gialong.classroom.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public PostLikeResponse likeOrUnlikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = authService.getCurrentUser();

        Optional<PostLike> postLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId());

        PostLikeResponse postLikeResponse = new PostLikeResponse();

        if(postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
        }
        else {
            PostLike postLikeEntity = PostLike.builder()
                    .post(post)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            postLikeRepository.save(postLikeEntity);
            postLikeResponse = toPostLikeResponse(postLikeEntity);
        }
        return postLikeResponse;
    }

    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    private PostLikeResponse toPostLikeResponse(PostLike postLike) {
        return PostLikeResponse.builder()
                .userId(postLike.getUser().getId())
                .postId(postLike.getPost().getId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }

    public Long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}