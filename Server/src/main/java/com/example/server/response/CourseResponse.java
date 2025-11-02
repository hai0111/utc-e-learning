package com.example.server.response;

import com.example.server.model.Courses;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CourseResponse {

    private UUID id;

    private String title;

    private String description;

    private Boolean isActive;

    private String instructor;

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
}
