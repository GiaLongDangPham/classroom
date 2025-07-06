package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.PostRequest;
import com.gialong.classroom.dto.post.PostResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Classroom;
import com.gialong.classroom.model.Post;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.ClassroomRepository;
import com.gialong.classroom.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ClassroomRepository classroomRepository;
    private final AuthService authService;

    public PostResponse createPost(PostRequest request) {
        User currentUser = authService.getCurrentUser();
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .classroom(classroom)
                .createdBy(currentUser)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
        return toPostResponse(post);
    }

    public List<PostResponse> getPostsByClassId(Long classId) {
        if (!classroomRepository.existsById(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        return postRepository.findByClassroomIdOrderByCreatedAtDesc(classId)
                .stream()
                .map(this::toPostResponse)
                .toList();
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return toPostResponse(post);
    }

    public PostResponse updatePost(Long id, PostRequest request) {
        User currentUser = authService.getCurrentUser();
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

    public void deletePost(Long id) {
        User currentUser = authService.getCurrentUser();
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DINED);
        }

        postRepository.delete(post);
    }

    private PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .createdBy(String.format("%s %s", post.getCreatedBy().getFirstName(), post.getCreatedBy().getLastName()))
                .classroomId(post.getClassroom().getId())
                .build();
    }
}