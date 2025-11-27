package com.example.server.model;

import com.example.server.enums.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "Lessons")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Lessons {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "CourseId", referencedColumnName = "Id")
    private Courses course;

    @Column(name = "Title")
    private String title;

    @Column(name = "Url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "lessonType")
    private LessonType lessonType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuizId", referencedColumnName = "Id")
    private Quizzes quizzes;

    @Column(name = "OrderIndex")
    private Integer orderIndex;

    @Column(name = "IsActive")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private Users createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private Users updatedBy;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
