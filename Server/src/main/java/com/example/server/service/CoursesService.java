package com.example.server.service;

import com.example.server.dto.CoursesDto;
import com.example.server.dto.StudentDto;
import com.example.server.model.Courses;
import com.example.server.request.CoursesRequest;
import com.example.server.response.ApiResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CoursesService {

    Page<CoursesDto> getPageCourses(int page, int size);

    ApiResponse<CoursesDto> getCourse(UUID courseId);

    ApiResponse<Courses> createCourse(CoursesRequest coursesRequest);

    ApiResponse<Courses> editCourse(CoursesRequest coursesRequest, UUID courseId);

    ApiResponse<Courses> hiddenCourse(UUID courseId);

    Page<StudentDto> getPageStudentsOfCourse(int page, int size, UUID courseId);

    Page<StudentDto> getPageStudentsNotCourse(int page, int size, UUID courseId);

    ApiResponse<Void> addStudentToCourse(UUID courseId, List<UUID> studentIds);

    ApiResponse<Void> removeStudentFromCourse(UUID courseId, UUID studentId);
}
