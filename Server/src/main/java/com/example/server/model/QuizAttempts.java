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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "QuizAttempts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "EnrollmentId", referencedColumnName = "Id")
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "QuizId", referencedColumnName = "Id")
    private Quizzes quizzes;

    @Column(name = "AttemptNo")
    private Integer attemptNo;

    @Column(name = "FeedBack")
    private String feedBack;

    @Column(name = "CreatedAt")
    private Date createdAt;
}
