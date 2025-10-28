package com.example.server.service.impl;

import com.example.server.enums.LessonType;
import com.example.server.enums.Role;
import com.example.server.model.Courses;
import com.example.server.model.Lessons;
import com.example.server.model.Users;
import com.example.server.repository.CoursesRepository;
import com.example.server.repository.LessonRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.response.MessageResponse;

import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.CoursesService;
import com.example.server.service.LessonService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CoursesRepository courseRepository;

    private Role checkRole(Authentication authentication) {
        System.out.println("=== ROLE DETECTION DEBUG ===");

        authentication.getAuthorities().forEach(auth -> {
            System.out.println("Authority: " + auth.getAuthority());
        });

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN");

        System.out.println("Detected role string: " + role);

        // Fix: Xử lý cả "ROLE_ADMIN" và "ADMIN"
        if (role.contains("ADMIN")) {
            System.out.println("Returning: ADMIN");
            return Role.ADMIN;
        } else if (role.contains("INSTRUCTOR")) {
            System.out.println("Returning: INSTRUCTOR");
            return Role.INSTRUCTOR;
        } else {
            System.out.println("Returning: STUDENT");
            return Role.STUDENT;
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @Override
    public List<LessonResponse> getListLessons(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        switch (role) {
            case ADMIN:
                return lessonRepository.findByCourseIdAndIsActiveTrueOrderByOrderIndexAsc(courseId);
            case INSTRUCTOR:
                return lessonRepository.findByCourseIdAndInstructorIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
            case STUDENT:
                return lessonRepository.findByCourseIdAndStudentIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
            default:
                throw new RuntimeException("Invalid role");
        }
    }

    @Override
    public LessonResponse getLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        Optional<LessonResponse> lessonResponse;
        switch (role) {
            case ADMIN:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndIsActiveTrue(lessonId, courseId);
                break;
            case INSTRUCTOR:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId);
                break;
            case STUDENT:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndStudentIdAndIsActiveTrue(lessonId, courseId, currentUserId);
                break;
            default:
                throw new RuntimeException("Invalid role");
        }

        return lessonResponse.orElse(null);
    }

    @Override
    public MessageResponse createLesson(LessonRequest lessonRequest, UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not an instructor", 403);
        }

        // Check if course exists
        Optional<Courses> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return new MessageResponse("Course not found", 404);
        }

        // Check permission for instructor
        if (role.equals(Role.INSTRUCTOR)) {
            if (!course.get().getUsers().getId().equals(currentUserId)) {
                return new MessageResponse("You don't have permission to create lesson in this course", 403);
            }
        }

        // Check if lesson with same title already exists
        if (lessonRepository.existsByCourseIdAndTitleAndIsActiveTrue(courseId, lessonRequest.getTitle())) {
            return new MessageResponse("Lesson with this title already exists in the course", 400);
        }

        Lessons lesson = new Lessons();
        lesson.setCourse(course.get());
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setUrl(lessonRequest.getUrl());
        lesson.setLessonType(lessonRequest.getType());
        lesson.setOrderIndex(lessonRequest.getOrderIndex() != null ?
                lessonRequest.getOrderIndex() :
                lessonRepository.findMaxOrderIndexByCourseId(courseId) + 1);
        lesson.setIsActive(true);
        lesson.setCreatedBy(user);
        lesson.setUpdatedBy(user);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());

        lessonRepository.save(lesson);
        return new MessageResponse("Create lesson successfully", 200);
    }

    @Override
    public MessageResponse editLesson(LessonRequest lessonRequest, UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not an instructor", 403);
        }

        // Find lesson
        Optional<Lessons> lessonOptional;
        if (role.equals(Role.ADMIN)) {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndIsActiveTrue(lessonId, courseId);
        } else {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId);
        }

        if (lessonOptional.isEmpty()) {
            return new MessageResponse("Lesson not found", 404);
        }

        Lessons lesson = lessonOptional.get();

        // Check if title is being changed and conflicts with existing lesson
        if (!lesson.getTitle().equals(lessonRequest.getTitle()) &&
                lessonRepository.existsByCourseIdAndTitleAndIdNotAndIsActiveTrue(courseId, lessonRequest.getTitle(), lessonId)) {
            return new MessageResponse("Lesson with this title already exists in the course", 400);
        }

        lesson.setTitle(lessonRequest.getTitle());
        lesson.setUrl(lessonRequest.getUrl());
        lesson.setLessonType(lessonRequest.getType());
        if (lessonRequest.getOrderIndex() != null) {
            lesson.setOrderIndex(lessonRequest.getOrderIndex());
        }
        lesson.setUpdatedBy(user);
        lesson.setUpdatedAt(LocalDateTime.now());

        lessonRepository.save(lesson);
        return new MessageResponse("Update lesson successfully", 200);
    }

    @Override
    public MessageResponse hiddenLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            return new MessageResponse("User does not exist", 400);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            return new MessageResponse("This is not an instructor", 403);
        }

        // Find lesson
        Optional<Lessons> lessonOptional;
        if (role.equals(Role.ADMIN)) {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndIsActiveTrue(lessonId, courseId);
        } else {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId);
        }

        if (lessonOptional.isEmpty()) {
            return new MessageResponse("Lesson not found", 404);
        }

        Lessons lesson = lessonOptional.get();
        lesson.setIsActive(false);
        lesson.setUpdatedBy(user);
        lesson.setUpdatedAt(LocalDateTime.now());
        lessonRepository.save(lesson);

        return new MessageResponse("Hide lesson successfully", 200);
    }

    @Override
    public MessageResponse deleteLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Only admin can hard delete
        if (!checkRole(authentication).equals(Role.ADMIN)) {
            return new MessageResponse("Only admin can delete lesson", 403);
        }

        Optional<Lessons> lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId);
        if (lesson.isEmpty()) {
            return new MessageResponse("Lesson not found", 404);
        }

        lessonRepository.delete(lesson.get());
        return new MessageResponse("Delete lesson successfully", 200);
    }

    @Override
    @Transactional
    public BatchUpdateResponse updateLessonBatch(UpdateLessonBatchRequest request, UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = getCurrentUserId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            return new BatchUpdateResponse("User does not exist", 400, 0, 0, List.of());
        }
        if (user.getRole().equals(Role.STUDENT)) {
            return new BatchUpdateResponse("This is not an instructor", 403, 0, 0, List.of());
        }

        List<BatchUpdateResponse.BatchUpdateError> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // Validate và update từng lesson
        for (UpdateLessonBatchRequest.UpdateLessonItem item : request.getLessons()) {
            try {
                // Parse lesson ID
                UUID lessonId = UUID.fromString(item.getId());

                // Find lesson với phân quyền
                Optional<Lessons> lessonOptional;
                if (role.equals(Role.ADMIN)) {
                    lessonOptional = lessonRepository.findLessonEntityByIdAndCourseId(lessonId, courseId);
                } else {
                    lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorId(lessonId, courseId, currentUserId);
                }

                if (lessonOptional.isEmpty()) {
                    errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Lesson not found or no permission"));
                    failureCount++;
                    continue;
                }

                Lessons lesson = lessonOptional.get();
                boolean hasChanges = false;

                // Update orderIndex nếu có giá trị
                if (item.getOrderIndex() != null && !item.getOrderIndex().equals(lesson.getOrderIndex())) {
                    // Check duplicate orderIndex trong batch
                    boolean hasDuplicateInBatch = request.getLessons().stream()
                            .filter(other -> other != item && other.getOrderIndex() != null)
                            .anyMatch(other -> other.getOrderIndex().equals(item.getOrderIndex()));

                    if (hasDuplicateInBatch) {
                        errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Duplicate orderIndex in batch"));
                        failureCount++;
                        continue;
                    }

                    // Check duplicate orderIndex trong database
                    boolean existsInDb = lessonRepository.existsByCourseIdAndOrderIndexAndIdNot(courseId, item.getOrderIndex(), lessonId);
                    if (existsInDb) {
                        errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Order index already exists in course"));
                        failureCount++;
                        continue;
                    }

                    lesson.setOrderIndex(item.getOrderIndex());
                    hasChanges = true;
                }

                // Update isActive nếu có giá trị
                if (item.getIsActive() != null && !item.getIsActive().equals(lesson.getIsActive())) {
                    lesson.setIsActive(item.getIsActive());
                    hasChanges = true;
                }

                if (hasChanges) {
                    lesson.setUpdatedBy(user);
                    lesson.setUpdatedAt(LocalDateTime.now());
                    lessonRepository.save(lesson);
                    successCount++;
                } else {
                    successCount++; // Không có changes vẫn tính là success
                }

            } catch (IllegalArgumentException e) {
                errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Invalid lesson ID format"));
                failureCount++;
            } catch (Exception e) {
                errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Internal server error: " + e.getMessage()));
                failureCount++;
            }
        }

        String message = String.format("Batch update completed. Success: %d, Failure: %d", successCount, failureCount);
        return new BatchUpdateResponse(message, 200, successCount, failureCount, errors);
    }
}
