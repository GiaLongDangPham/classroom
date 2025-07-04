package com.gialong.classroom.repository;

import com.gialong.classroom.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}