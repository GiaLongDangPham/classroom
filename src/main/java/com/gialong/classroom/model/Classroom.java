package com.gialong.classroom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private User createdBy;

    @OneToMany(mappedBy = "classroom")
    @JsonManagedReference
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "classroom")
    @JsonManagedReference
    private List<Post> posts;

    @OneToMany(mappedBy = "classroom")
    @JsonManagedReference
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "classroom")
    @JsonManagedReference
    private List<ChatMessage> chatMessages;
}