package com.example.server.model;

import com.example.server.enums.LessonType;
import jakarta.persistence.*;
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

    @Column(name = "IsActive")
    private String isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "lessonType")
    private LessonType lessonType;

    @Column(name = "OrderIndex")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", referencedColumnName = "Id")
    private Users createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UpdatedBy", referencedColumnName = "Id")
    private Users updatedBy;

    @Column(name = "CreateAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
