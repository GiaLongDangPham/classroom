package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.post.PostRequest;
import com.gialong.classroom.dto.post.PostResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.repository.PostRepository;
import com.gialong.classroom.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ClassroomRepository classroomRepository;
    private final AuthServiceImpl authServiceImpl;

    @Override
    public PostResponse createPost(PostRequest request) {
        User currentUser = authServiceImpl.getCurrentUser();
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .commentCount(0L)
                .classroom(classroom)
                .createdBy(currentUser)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return toPostResponse(post);
    }

    @Override
    public List<PostResponse> getPostsByClassId(Long classId) {
        if (!classroomRepository.existsById(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        return postRepository.findByClassroomIdOrderByCreatedAtDesc(classId)
                .stream()
                .map(this::toPostResponse)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return toPostResponse(post);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest request) {
        User currentUser = authServiceImpl.getCurrentUser();
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postRepository.save(post);

        return toPostResponse(post);
    }

    @Override
    public void deletePost(Long id) {
        User currentUser = authServiceImpl.getCurrentUser();
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        postRepository.delete(post);
    }

    public PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .avatarUrl(post.getCreatedBy().getAvatarUrl())
                .createdAt(post.getCreatedAt())
                .createdBy(String.format("%s %s", post.getCreatedBy().getFirstName(), post.getCreatedBy().getLastName()))
                .classroomId(post.getClassroom().getId())
                .build();
    }
}