package com.example.server.service.impl;

import com.example.server.exception.CustomServiceException;
import com.example.server.model.Courses;
import com.example.server.model.Users;
import com.example.server.repository.CoursesRepository;
import com.example.server.repository.EnrollmentRepository;
import com.example.server.repository.LessonRepository;
import com.example.server.repository.ProgressRepository;
import com.example.server.repository.UserPositionRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.response.CourseProgressResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CoursesRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserPositionRepository userPositionRepository;
    private final UsersRepository usersRepository;

    @Override
    public CourseProgressResponse getCourseProgress(UUID courseId, UUID studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomServiceException("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();

        if (!currentUserId.equals(studentId)) {
            throw new CustomServiceException("You can only view your own progress", HttpStatus.FORBIDDEN);
        }

        // VALIDATION (Check Enrollment)
        boolean isEnrolled = enrollmentRepository.existsByCourse_IdAndUsers_Id(courseId, studentId);
        if (!isEnrolled) {
            throw new CustomServiceException("Student is not enrolled in this course", HttpStatus.BAD_REQUEST);
        }

        // Get Information
        Courses course = courseRepository.findById(courseId).orElseThrow(() -> new CustomServiceException("Course not found", HttpStatus.NOT_FOUND));
        // Get Instructor
        Users instructor = course.getUsers();
        if (instructor == null) {
            instructor = new Users();
            instructor.setId(UUID.randomUUID());
            instructor.setName("Unknown Instructor");
        }
        // Get total Lessons and completed Lessons
        long totalLessons = lessonRepository.countByCourseIdAndIsActiveTrueAndIsDeleteFalse(courseId);
        long completedLessons = progressRepository.countCompletedLessonsByCourseAndStudent(courseId, studentId);
        // Calculate percentage
        double percentage = 0.0;
        if (totalLessons > 0) {
            percentage = ((double) completedLessons / totalLessons) * 100;
        }
        percentage = Math.round(percentage * 10.0) / 10.0;
        // Get positions,total courses, total students of Instructor
        List<String> positions = userPositionRepository.findPositionNamesByUserId(instructor.getId());
        String positionName = positions.isEmpty() ? "Instructor" : positions.get(0);

        long totalCoursesOfInstructor = courseRepository.countByUsers_Id(instructor.getId());
        long totalStudentsOfInstructor = enrollmentRepository.countTotalStudentsByInstructor(instructor.getId());
        // Format Date
        String formattedDate = null;
        if (course.getUpdatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            formattedDate = sdf.format(course.getUpdatedAt());
        } else {
            formattedDate = "N/A";
        }

        return CourseProgressResponse.builder().courseId(course.getId()).courseTitle(course.getTitle()).description(course.getDescription()).updatedAt(formattedDate).totalLessons((int) totalLessons).completedLessons((int) completedLessons).progressPercent(percentage).instructorName(instructor.getName()).instructorPosition(positionName).instructorTotalCourses(totalCoursesOfInstructor).instructorTotalStudents(totalStudentsOfInstructor).build();
    }
}
