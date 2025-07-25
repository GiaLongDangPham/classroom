package com.gialong.classroom.repository;

import com.gialong.classroom.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId);
    Long countByPostId(Long postId);
}