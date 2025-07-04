package com.gialong.classroom.repository;

import com.gialong.classroom.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByJoinCode(String joinCode);
}