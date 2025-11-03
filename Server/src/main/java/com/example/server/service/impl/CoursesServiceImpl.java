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
import com.example.server.response.CourseResponse;
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

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
    public ApiResponse<List<CoursesDto>> getPageCourses(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<CoursesDto> pageCourse;
        if (checkRole(authentication).equals(Role.ADMIN)) {
            pageCourse = coursesRepository.findAllCourse(pageable);
            return new ApiResponse<>(200, "Success", pageCourse.getContent());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            pageCourse = coursesRepository.findAllCourseByInstructor(pageable, userDetails.getId());
            return new ApiResponse<>(200, "Success", pageCourse.getContent());
        } else {
            pageCourse = coursesRepository.findAllCourseByStudent(pageable, userDetails.getId());
            return new ApiResponse<>(200, "Success", pageCourse.getContent());
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
            throw new CustomServiceException("No data", HttpStatus.NOT_FOUND);
        }
        return new ApiResponse<>(200, "Success", coursesDto);
    }

    @Override
    public ApiResponse<CourseResponse> createCourse(CoursesRequest coursesRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users users = usersRepository.findByIdAndIsActive(userDetails.getId(), true);
        if (users == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }
        Courses courses = new Courses();
        if (checkRole(authentication).equals(Role.ADMIN) && coursesRequest.getUserId().toString().isEmpty()) {
            courses.setUsers(Users.builder().id(coursesRequest.getUserId()).build());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            courses.setUsers(Users.builder().id(userDetails.getId()).build());
        }
        courses.setIsActive(coursesRequest.getIsActive());
        courses.setTitle(coursesRequest.getTitle());
        courses.setDescription(coursesRequest.getDescription());
        courses.setIsActive(coursesRequest.getIsActive());
        courses.setCreatedAt(new Date());
        courses.setUpdatedAt(new Date());
        Courses savedCourse = coursesRepository.save(courses);
        CourseResponse courseResponse = CourseResponse.fromEntityToResponse(savedCourse, userDetails.getName());
        return new ApiResponse<>(200, "Create course successfully", courseResponse);
    }

    @Override
    public ApiResponse<CourseResponse> editCourse(CoursesRequest coursesRequest, UUID courseId) {
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
            courses.setUsers(Users.builder().id(coursesRequest.getUserId()).build());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            if (!courses.getUsers().getId().equals(userDetails.getId())) {
                throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
            }
            courses.setUsers(Users.builder().id(userDetails.getId()).build());
        }
        courses.setIsActive(coursesRequest.getIsActive());
        courses.setTitle(coursesRequest.getTitle());
        courses.setDescription(coursesRequest.getDescription());
        courses.setUpdatedAt(new Date());
        Courses savedCourse = coursesRepository.save(courses);
        CourseResponse courseResponse = CourseResponse.fromEntityToResponse(savedCourse, userDetails.getName());
        return new ApiResponse<>(200, "Update course successfully", courseResponse);
    }

    @Override
    public ApiResponse<List<StudentDto>> getPageStudentsOfCourse(int page, int size, UUID courseId) {
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
        Page<StudentDto> result = usersRepository.findAllStudentsOfCourse(courseId, pageable);
        return new ApiResponse<>(200, "Success", result.getContent());
    }

    @Override
    public ApiResponse<List<StudentDto>> getPageStudentsNotCourse(int page, int size, UUID courseId) {
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
        Page<StudentDto> result = usersRepository.findAllStudentsNotCourse(courseId, userDetails.getId(), pageable);
        return new ApiResponse<>(200, "Success", result.getContent());
    }

    @Override
    public ApiResponse<Void> addStudentToCourse(UUID courseId, List<UUID> studentIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.STUDENT)) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }

        if (!courses.getUsers().getId().equals(userDetails.getId())) {
            throw new CustomServiceException("This account does not manage this course", HttpStatus.FORBIDDEN);
        }

        // Get all active student IDs
        List<UUID> allStudentIdsList = usersRepository.findAllStudentsId();

        // Check if student ID exists
        // If there is a student ID that does not exist (meaning it is not in the list of all student IDs), then immediately stop the loop.
        for (UUID studentId : studentIds) {
            if (!allStudentIdsList.contains(studentId)) {
                throw new CustomServiceException("One of the student accounts does not exist.", HttpStatus.NOT_FOUND);
            }
        }

        // Get all student IDs that have registered for the course and convert them to Set
        List<UUID> studentIdsListOfCourse = usersRepository.findAllStudentIdsOfCourse(courseId);
        Set<UUID> enrolledStudentIdsSet = new HashSet<>(studentIdsListOfCourse);

        // Check if any student ID has registered for the course then stop checking and report an error
        for (UUID studentId : studentIds) {
            if (enrolledStudentIdsSet.contains(studentId)) {
                throw new CustomServiceException("One of the students is already enrolled in this course.", HttpStatus.BAD_REQUEST);
            }
        }

        // If the ID passes the 2 rounds of checking, the data will be added to the Enrollment table.
        List<Enrollment> listEnrollments = new ArrayList<>();
        for (UUID studentId : studentIds) {
            Enrollment enrollment = new Enrollment();
            enrollment.setUsers(Users.builder().id(studentId).build());
            enrollment.setEnrolledAt(new Date());
            enrollment.setIsActive(true);
            enrollment.setCourse(Courses.builder().id(courseId).build());
            listEnrollments.add(enrollment);
        }

        enrollmentRepository.saveAll(listEnrollments);
        return new ApiResponse<>(200, "Add student successfully", null);
    }

    @Override
    public ApiResponse<Void> removeStudentFromCourse(UUID courseId, UUID studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.STUDENT)) {
            throw new CustomServiceException("No access", HttpStatus.FORBIDDEN);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            throw new CustomServiceException("This course does not exist", HttpStatus.NOT_FOUND);
        }

        if (!courses.getUsers().getId().equals(userDetails.getId())) {
            throw new CustomServiceException("This account does not manage this course", HttpStatus.FORBIDDEN);
        }
        Users users = usersRepository.findByIdAndIsActive(studentId, true);
        if (users == null) {
            throw new CustomServiceException("This student does not exist.", HttpStatus.NOT_FOUND);
        }
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollment == null) {
            throw new CustomServiceException("This student is not in this course", HttpStatus.NOT_FOUND);
        }
        enrollment.setIsActive(false);
        enrollmentRepository.save(enrollment);
        return new ApiResponse<>(200, "Remove student successfully", null);
    }
}