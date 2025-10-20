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

    @Column(name = "lessonType")
    private LessonType lessonType;

    @Column(name = "OrderIndex")
    private Integer orderIndex;

    @Column(name = "CreatedBy")
    private Date createdBy;

    @Column(name = "UpdatedBy")
    private Date updatedBy;

    @Column(name = "CreateAt")
    private Date createdAt;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
