package com.gialong.classroom.repository;

import com.gialong.classroom.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByClassroomId(Long classroomId);

    List<Post> findByClassroomIdOrderByCreatedAtDesc(Long classroomId);
}