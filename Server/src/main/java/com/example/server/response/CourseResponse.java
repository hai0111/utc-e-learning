package com.example.server.response;

import com.example.server.dto.CoursesDto;
import com.example.server.model.Courses;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CourseResponse {

    private UUID id;

    private String title;

    private String description;

    private Boolean isActive;

    private String instructor;

    private Long totalStudents;

    private Date createdAt;

    private Date updatedAt;

    public static CourseResponse fromEntityToResponse(Courses courses, String instructor) {
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setId(courses.getId());
        courseResponse.setTitle(courses.getTitle());
        courseResponse.setDescription(courses.getDescription());
        courseResponse.setIsActive(courses.getIsActive());
        courseResponse.setCreatedAt(courses.getCreatedAt());
        courseResponse.setUpdatedAt(courses.getUpdatedAt());
        courseResponse.setInstructor(instructor);
        return courseResponse;
    }

    public static CourseResponse convertToCourseResponse(CoursesDto courseDto, Long totalStudents) {
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setId(courseDto.getId());
        courseResponse.setTitle(courseDto.getTitle());
        courseResponse.setDescription(courseDto.getDescription());
        courseResponse.setIsActive(courseDto.getIsActive());
        courseResponse.setCreatedAt(courseDto.getCreatedAt());
        courseResponse.setUpdatedAt(courseDto.getUpdatedAt());
        courseResponse.setInstructor(courseDto.getInstructor());
        courseResponse.setTotalStudents(totalStudents);
        return courseResponse;
    }
}
