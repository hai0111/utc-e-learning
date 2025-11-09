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
@Table(name = "QuizOptions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "QuizId", referencedColumnName = "Id")
    private QuizQuestions quizQuestions;

    @Column(name = "QuestionText")
    private String questionText;

    @Column(name = "IsCorrect")
    private Boolean isCorrect;

    @Column(name = "OrderIndex")
    private Integer orderIndex;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
