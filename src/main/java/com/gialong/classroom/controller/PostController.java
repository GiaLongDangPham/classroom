package com.gialong.classroom.controller;

import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.post.PostRequest;
import com.gialong.classroom.dto.post.PostResponse;
import com.gialong.classroom.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseData<PostResponse> createPost(@RequestBody @Valid PostRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseData.<PostResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Post created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/class/{classId}")
    public ResponseData<List<PostResponse>> getPostsByClass(@PathVariable Long classId) {
        List<PostResponse> response = postService.getPostsByClassId(classId);
        return ResponseData.<List<PostResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Posts retrieved successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseData<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseData.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Post retrieved successfully")
                .data(response)
                .build();
    }

    //Có thể ko cần
    @PutMapping("/{id}")
    public ResponseData<PostResponse> updatePost(@PathVariable Long id,
                                                 @RequestBody @Valid PostRequest request) {
        PostResponse response = postService.updatePost(id, request);
        return ResponseData.<PostResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Post updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseData.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Post deleted successfully")
                .build();
    }
}