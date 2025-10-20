package com.example.server.model;

import com.example.server.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Progress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "EnrollmentId", referencedColumnName = "Id")
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "LessonId", referencedColumnName = "Id")
    private Lessons lesson;

    @Column(name = "ProgressStatus")
    private ProgressStatus progressStatus;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
