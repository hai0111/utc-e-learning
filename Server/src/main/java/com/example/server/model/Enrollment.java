package com.example.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "EnrolledAt")
    private Date enrolledAt;
}
