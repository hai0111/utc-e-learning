package com.example.server.model;

import com.example.server.enums.ProgressStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
    private Lessons lessons;

    @Enumerated(EnumType.STRING)
    @Column(name = "ProgressStatus")
    private ProgressStatus progressStatus;

    @Column(name = "UpdatedAt")
    private Date updatedAt;
}
