package com.example.server.service;

import com.example.server.dto.CoursesDto;
import com.example.server.request.CoursesRequest;
import com.example.server.response.MessageResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CoursesService {

    Page<CoursesDto> getPageCourses(int page, int size);

    CoursesDto getCourse(UUID courseId);

    MessageResponse createCourse(CoursesRequest coursesRequest);

    MessageResponse editCourse(CoursesRequest coursesRequest, UUID courseId);

    MessageResponse hiddenCourse(UUID courseId);
}
