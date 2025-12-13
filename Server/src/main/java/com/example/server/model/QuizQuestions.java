package com.example.server.model;

import com.example.server.enums.QuestionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "QuizQuestions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuizQuestions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "QuizzesId", referencedColumnName = "Id")
    private Quizzes quizzes;

    @Column(name = "QuestionText")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "QuestionType")
    private QuestionType questionType;

    @Column(name = "RawPoint")
    private Double rawPoint;

    @Column(name = "OrderIndex")
    private Integer orderIndex;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;

    @OneToMany(mappedBy = "quizQuestions", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizOptions> options;
}
