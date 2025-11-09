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
@Table(name = "QuizAnswers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "AttemptId", referencedColumnName = "Id")
    private QuizAttempts quizAttempts;

    @ManyToOne
    @JoinColumn(name = "QuestionId", referencedColumnName = "Id")
    private QuizQuestions quizQuestions;

    @ManyToOne
    @JoinColumn(name = "OptionId", referencedColumnName = "Id")
    private QuizOptions quizOptions;

    @Column(name = "RawText")
    private String rawText;

    @Column(name = "CreatedAt")
    private Date createdAt;
}
