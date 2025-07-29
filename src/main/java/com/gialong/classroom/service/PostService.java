package com.gialong.classroom.service;

import com.gialong.classroom.dto.post.PostRequest;
import com.gialong.classroom.dto.post.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request);

    List<PostResponse> getPostsByClassId(Long classId);

    PostResponse getPostById(Long id);

    PostResponse updatePost(Long id, PostRequest request);

    void deletePost(Long id);
}
