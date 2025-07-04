package com.gialong.classroom.repository;

import com.gialong.classroom.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    Boolean existsByUserIdAndClassroomId(Long userId, Long classroomId);
}