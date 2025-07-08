package com.gialong.classroom.repository;

import com.gialong.classroom.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByClassroomIdOrderBySentAtAsc(Long classroomId);
    Optional<ChatMessage> findTopByClassroomIdOrderBySentAtDesc(Long classroomId);
}