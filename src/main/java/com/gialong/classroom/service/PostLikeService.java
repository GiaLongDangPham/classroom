package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.PostResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.PostLike;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.PostLikeRepository;
import com.gialong.classroom.repository.PostRepository;
import com.gialong.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final AuthService authService;

    @Transactional
    public PostResponse likeOrUnlikePost(Long postId, String isLiked) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = authService.getCurrentUser();

        if (Objects.equals(isLiked, "false")) {
            postLikeRepository.deleteByPostIdAndUserId(postId, user.getId());

            post.setLikeCount(post.getLikeCount() - 1);
        }
        else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            postLikeRepository.save(like);

            post.setLikeCount(post.getLikeCount() + 1);
        }
        postRepository.save(post);
        return postService.toPostResponse(post);
    }

    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}