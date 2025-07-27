package com.gialong.classroom.controller.classroom;

import com.gialong.classroom.dto.post.postcomment.PostCommentResponse;
import com.gialong.classroom.dto.post.postlike.PostLikeResponse;
import com.gialong.classroom.service.classroom.PostCommentService;
import com.gialong.classroom.service.classroom.PostLikeService;
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

    @PostMapping("/likes/{postId}")
    public ResponseEntity<PostLikeResponse> likeOrUnlikePost(@PathVariable Long postId) {
        PostLikeResponse response = postLikeService.likeOrUnlikePost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes/{postId}/{userId}")
    public ResponseEntity<Boolean> isLiked(@PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(postLikeService.isLiked(postId, userId));
    }

    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(postLikeService.countLikes(postId));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<PostCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postCommentService.getComments(postId));
    }

    @GetMapping("/comments/count/{postId}")
    public ResponseEntity<Long> countComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postCommentService.countComments(postId));
    }

    @PostMapping("/comments/{postId}")
    public ResponseEntity<PostCommentResponse> addComment(@PathVariable Long postId,
                                                  @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return ResponseEntity.ok(postCommentService.addComment(postId, content));
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        postCommentService.deleteComment(commentId);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<PostCommentResponse> updateComment(@PathVariable Long commentId,
                                                             @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return ResponseEntity.ok(postCommentService.updateComment(commentId, content));
    }


}