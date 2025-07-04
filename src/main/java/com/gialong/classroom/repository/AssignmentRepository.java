package com.gialong.classroom.repository;

import com.gialong.classroom.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassroomId(Long classroomId);
}