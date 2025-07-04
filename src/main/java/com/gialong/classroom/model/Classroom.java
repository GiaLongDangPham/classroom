package com.gialong.classroom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String joinCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "classroom")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "classroom")
    private List<Post> posts;

    @OneToMany(mappedBy = "classroom")
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "classroom")
    private List<ChatMessage> chatMessages;
}