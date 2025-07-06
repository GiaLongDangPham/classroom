package com.gialong.classroom.controller;

import com.gialong.classroom.dto.post.PostResponse;
import com.gialong.classroom.dto.post.postcomment.PostCommentResponse;
import com.gialong.classroom.model.PostComment;
import com.gialong.classroom.service.PostCommentService;
import com.gialong.classroom.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostLikeService postLikeService;
    private final PostCommentService postCommentService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> likeOrUnlikePost(@PathVariable Long postId, @RequestParam String isLiked) {
        PostResponse response = postLikeService.likeOrUnlikePost(postId, isLiked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/{userId}/like")
    public ResponseEntity<Boolean> isLiked(@PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(postLikeService.isLiked(postId, userId));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<PostCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postCommentService.getComments(postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostCommentResponse> addComment(@PathVariable Long postId,
                                                  @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return ResponseEntity.ok(postCommentService.addComment(postId, content));
    }




}