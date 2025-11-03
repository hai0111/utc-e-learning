package com.example.server.service;

import com.example.server.dto.CoursesDto;
import com.example.server.dto.StudentDto;
import com.example.server.request.CoursesRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.CourseResponse;

import java.util.List;
import java.util.UUID;

public interface CoursesService {

    ApiResponse<List<CourseResponse>> getPageCourses(int page, int size);

    ApiResponse<CoursesDto> getCourse(UUID courseId);

    ApiResponse<CourseResponse> createCourse(CoursesRequest coursesRequest);

    ApiResponse<CourseResponse> editCourse(CoursesRequest coursesRequest, UUID courseId);

    ApiResponse<List<StudentDto>> getPageStudentsOfCourse(int page, int size, UUID courseId);

    ApiResponse<List<StudentDto>> getPageStudentsNotCourse(int page, int size, UUID courseId);

    ApiResponse<Void> addStudentToCourse(UUID courseId, List<UUID> studentIds);

    ApiResponse<Void> removeStudentFromCourse(UUID courseId, UUID studentId);
}
