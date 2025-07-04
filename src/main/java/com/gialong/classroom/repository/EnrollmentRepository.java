package com.gialong.classroom.repository;

import com.gialong.classroom.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    List<Enrollment> findByClassroomId(Long classroomId);

    Boolean existsByUserIdAndClassroomId(Long userId, Long classroomId);
    Optional<Enrollment> findByUserIdAndClassroomId(Long userId, Long classroomId);

}