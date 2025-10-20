package com.example.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Enrollment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "StudentId", referencedColumnName = "Id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    private Courses course;

    @Column(name = "EnrolledAt")
    private Date enrolledAt;
}
