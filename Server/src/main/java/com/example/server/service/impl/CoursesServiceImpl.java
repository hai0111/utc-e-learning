package com.example.server.service.impl;

import com.example.server.dto.CoursesDto;
import com.example.server.enums.Role;
import com.example.server.model.Courses;
import com.example.server.model.Users;
import com.example.server.repository.CoursesRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.CoursesRequest;
import com.example.server.response.MessageResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private UsersRepository usersRepository;

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
    public CoursesDto getCourse(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.ADMIN)) {
            return coursesRepository.findCourseByIdAndRoleAdmin(courseId);
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            return coursesRepository.findCourseByIdAndInstructorId(courseId, userDetails.getId());
        } else {
            return coursesRepository.findCourseByIdAndStudentId(courseId, userDetails.getId());
        }
    }

    @Override
    public MessageResponse createCourse(CoursesRequest coursesRequest) {
        Users users = usersRepository.findByIdAndIsActive(coursesRequest.getUserId(), true);
        if (users == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not a instructor", 403);
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
        coursesRepository.save(courses);
        return new MessageResponse("Create course successfully", 200);
    }

    @Override
    public MessageResponse editCourse(CoursesRequest coursesRequest, UUID courseId) {
        Users users = usersRepository.findByIdAndIsActive(coursesRequest.getUserId(), true);
        if (users == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not a instructor", 403);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            return new MessageResponse("This course does not exist", 404);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (checkRole(authentication).equals(Role.ADMIN)) {
            courses.setUsers(Users.builder().id(coursesRequest.getUserId()).build());
            courses.setIsActive(coursesRequest.getIsActive());
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR)) {
            courses.setUsers(Users.builder().id(userDetails.getId()).build());
            courses.setIsActive(true);
        }
        courses.setTitle(coursesRequest.getTitle());
        courses.setDescription(coursesRequest.getDescription());
        courses.setUpdatedAt(new Date());
        coursesRepository.save(courses);
        return new MessageResponse("Update course successfully", 200);
    }

    @Override
    public MessageResponse hiddenCourse(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Users users = usersRepository.findByIdAndIsActive(userDetails.getId(), true);
        if (users == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (users.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not a instructor", 403);
        }
        Courses courses = coursesRepository.findByIdAndIsActive(courseId, true);
        if (courses == null) {
            return new MessageResponse("This course does not exist", 404);
        }
        if (checkRole(authentication).equals(Role.ADMIN)) {
            courses.setIsActive(false);
        } else if (checkRole(authentication).equals(Role.INSTRUCTOR) || checkRole(authentication).equals(Role.STUDENT)) {
            return new MessageResponse("Cannot hide course", 403);
        }
        coursesRepository.save(courses);
        return new MessageResponse("Hide course successfully", 200);
    }
}
