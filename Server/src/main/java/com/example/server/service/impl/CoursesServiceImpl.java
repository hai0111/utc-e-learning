package com.example.server.service.impl;

import com.example.server.dto.CoursesDto;
import com.example.server.dto.StudentDto;
import com.example.server.dto.UsersDto;
import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.Courses;
import com.example.server.model.Enrollment;
import com.example.server.model.Users;
import com.example.server.repository.CoursesRepository;
import com.example.server.repository.EnrollmentRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.CoursesRequest;
import com.example.server.response.ApiResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Role checkRole(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        return switch (Objects.requireNonNull(role).toUpperCase()) {
            case "ADMIN" -> Role.ADMIN;
            case "INSTRUCTOR" -> Role.INSTRUCTOR;
            default -> Role.STUDENT;
        };
    }

    @Override
    public Page<CoursesDto> getPageCourses(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        if (checkRole(authentication).equals(Role.ADMIN)) {
            return coursesRepository.findAllCourse(pageable);
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            return coursesRepository.findAllCourseByInstructor(pageable, userDetails.getId());
        } else {
            return coursesRepository.findAllCourseByStudent(pageable, userDetails.getId());
        }
    }

    @Override
    public ApiResponse<CoursesDto> getCourse(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CoursesDto coursesDto;
        if (checkRole(authentication).equals(Role.ADMIN)) {
            coursesDto = coursesRepository.findCourseByIdAndRoleAdmin(courseId);
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            coursesDto = coursesRepository.findCourseByIdAndInstructorId(courseId, userDetails.getId());
        } else {
            coursesDto = coursesRepository.findCourseByIdAndStudentId(courseId, userDetails.getId());
        }
        if (coursesDto == null) {
            return new ApiResponse<>(200, "No data", coursesDto);
        }
        return new ApiResponse<>(200, "OK", coursesDto);
    }

    @Override
    public ApiResponse<Courses> createCourse(CoursesRequest coursesRequest) {
        Users users = usersRepository.findByIdAndIsActive(coursesRequest.getUserId(), true);
        if (users == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Courses courses = new Courses();
        if (checkRole(authentication).equals(Role.ADMIN)) {
            courses.setUsers(Users.builder().id(coursesRequest.getUserId()).build());
            courses.setIsActive(coursesRequest.getIsActive());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            courses.setUsers(Users.builder().id(userDetails.getId()).build());
            courses.setIsActive(true);
        }
        courses.setTitle(coursesRequest.getTitle());
        courses.setDescription(coursesRequest.getDescription());
        courses.setIsActive(coursesRequest.getIsActive());
        courses.setCreatedAt(new Date());
        courses.setUpdatedAt(new Date());
        Courses savedCourse = coursesRepository.save(courses);
        return new ApiResponse<>(200, "Create course successfully", savedCourse);
    }

    @Override
    public ApiResponse<Courses> editCourse(CoursesRequest coursesRequest, UUID courseId) {
        Users users = usersRepository.findByIdAndIsActive(coursesRequest.getUserId(), true);
        if (users == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This is not an instructor", HttpStatus.FORBIDDEN);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.ADMIN)) {
            courses.setUsers(Users.builder().id(coursesRequest.getUserId()).build());
            courses.setIsActive(coursesRequest.getIsActive());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            if (courses.getUsers().getId().equals(userDetails.getId())) {
                throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
            }
            courses.setUsers(Users.builder().id(userDetails.getId()).build());
            courses.setIsActive(true);
        }
        courses.setTitle(coursesRequest.getTitle());
        courses.setDescription(coursesRequest.getDescription());
        courses.setUpdatedAt(new Date());
        Courses savedCourse = coursesRepository.save(courses);
        return new ApiResponse<>(200, "Update course successfully", savedCourse);
    }

    @Override
    public ApiResponse<Courses> hiddenCourse(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users users = usersRepository.findByIdAndIsActive(userDetails.getId(), true);
        if (users == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This is not an instructor", HttpStatus.FORBIDDEN);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }
        if (checkRole(authentication).equals(Role.ADMIN)) {
            courses.setIsActive(false);
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR) || checkRole(authentication).equals(Role.STUDENT)) {
            throw new CustomServiceException("Cannot hide course", HttpStatus.FORBIDDEN);
        }
        Courses savedCourse = coursesRepository.save(courses);
        return new ApiResponse<>(200, "Update course successfully", savedCourse);
    }

    @Override
    public Page<StudentDto> getPageStudentsOfCourse(int page, int size, UUID courseId) {
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UsersDto usersDto = usersRepository.findByCourseAndInstructorId(courses.getId(), userDetails.getId());
        if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            if (usersDto == null) {
                throw new CustomServiceException("This account does not have permission to operate", HttpStatus.FORBIDDEN);
            }
        }
        if (checkRole(authentication).equals(Role.STUDENT)) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        } else if (userDetails.getIsActive() == false) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        return usersRepository.findAllStudentsOfCourse(courseId, pageable);
    }

    @Override
    public Page<StudentDto> getPageStudentsNotCourse(int page, int size, UUID courseId) {
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.STUDENT)) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        } else if (userDetails.getIsActive() == false) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        return usersRepository.findAllStudentsNotCourse(courseId, userDetails.getId(), pageable);
    }

}