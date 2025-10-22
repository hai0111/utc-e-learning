package com.example.server.service;

import com.example.server.dto.CoursesDto;
import com.example.server.model.Courses;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CoursesService {

    Page<CoursesDto> getPageCourses(int page, int size);
}
