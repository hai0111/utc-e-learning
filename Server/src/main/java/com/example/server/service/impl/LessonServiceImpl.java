package com.example.server.service.impl;

import com.example.server.enums.Role;
import com.example.server.exception.CustomServiceException;
import com.example.server.model.Courses;
import com.example.server.model.Lessons;
import com.example.server.model.Users;
import com.example.server.repository.CoursesRepository;
import com.example.server.repository.LessonRepository;
import com.example.server.repository.UsersRepository;
import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.response.ApiResponse;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.security.service.UserDetailsImpl;
import com.example.server.service.CloudinaryService;
import com.example.server.service.LessonService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final UsersRepository usersRepository;
    private final CoursesRepository courseRepository;
    private final CloudinaryService cloudinaryService;

    private Role checkRole(Authentication authentication) {
        String role = authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
        return switch (Objects.requireNonNull(role).toUpperCase()) {
            case "ADMIN" -> Role.ADMIN;
            case "INSTRUCTOR" -> Role.INSTRUCTOR;
            default -> Role.STUDENT;
        };
    }

    @Override
    public ApiResponse<List<LessonResponse>> getListLessons(UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        List<LessonResponse> lessons;
        switch (role) {
            case ADMIN:
                lessons = lessonRepository.findByCourseIdAndIsActiveTrueOrderByOrderIndexAsc(courseId);
                break;
            case INSTRUCTOR:
                lessons = lessonRepository.findByCourseIdAndInstructorIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
                break;
            case STUDENT:
                lessons = lessonRepository.findByCourseIdAndStudentIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
                break;
            default:
                throw new CustomServiceException("Invalid role", HttpStatus.FORBIDDEN);
        }

        return new ApiResponse<>(200, "Success", lessons);
    }

    @Override
    public ApiResponse<LessonResponse> getLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        LessonResponse lessonResponse;
        switch (role) {
            case ADMIN:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndIsActiveTrue(lessonId, courseId).orElse(null);
                break;
            case INSTRUCTOR:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId).orElse(null);
                break;
            case STUDENT:
                lessonResponse = lessonRepository.findByIdAndCourseIdAndStudentIdAndIsActiveTrue(lessonId, courseId, currentUserId).orElse(null);
                break;
            default:
                throw new CustomServiceException("Invalid role", HttpStatus.FORBIDDEN);
        }

        if (lessonResponse == null) {
            throw new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND);
        }

        return new ApiResponse<>(200, "Get lesson successfully", lessonResponse);
    }

    @Override
    public ApiResponse<Object> createLesson(LessonRequest lessonRequest, UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }

        // Check if course exists
        Courses course = courseRepository.findByIdAndIsActive(courseId, true);
        if (course == null) {
            throw new CustomServiceException("Course not found", HttpStatus.NOT_FOUND);
        }

        // Check permission for instructor
        if (role.equals(Role.INSTRUCTOR)) {
            if (!course.getUsers().getId().equals(currentUserId)) {
                throw new CustomServiceException("You don't have permission to create lesson in this course", HttpStatus.FORBIDDEN);
            }
        }

        // Check if lesson with same title already exists
        if (lessonRepository.existsByCourseIdAndTitleAndIsActiveTrue(courseId, lessonRequest.getTitle())) {
            throw new CustomServiceException("Lesson with this title already exists in the course", HttpStatus.BAD_REQUEST);
        }

        // Validate file
        MultipartFile file = lessonRequest.getFile();
        if (file == null || file.isEmpty()) {
            throw new CustomServiceException("File is required", HttpStatus.BAD_REQUEST);
        }

        // Validate file size (max 100MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new CustomServiceException("File size must be less than 100MB", HttpStatus.BAD_REQUEST);
        }

        String cloudinaryUrl;
        try {
            // Upload file to Cloudinary
            String folderName = "course-" + courseId.toString();
            cloudinaryUrl = cloudinaryService.uploadFile(file, folderName);
        } catch (Exception e) {
            throw new CustomServiceException("Failed to upload file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            // Get the maximum orderIndex for this course and increment by 1
            Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByCourseId(courseId);
            Integer newOrderIndex = (maxOrderIndex != null ? maxOrderIndex : 0) + 1;

            // Create lesson entity
            Lessons lesson = new Lessons();
            lesson.setCourse(course);
            lesson.setTitle(lessonRequest.getTitle());
            lesson.setUrl(cloudinaryUrl);
            lesson.setLessonType(lessonRequest.getType());
            lesson.setOrderIndex(newOrderIndex);
            lesson.setIsActive(true);
            lesson.setCreatedBy(user);
            lesson.setUpdatedBy(user);
            lesson.setCreatedAt(new Date());
            lesson.setUpdatedAt(new Date());

            Lessons savedLesson = lessonRepository.save(lesson);

            // Convert to response using static method
            LessonResponse response = LessonResponse.convertToResponse(savedLesson);

            return new ApiResponse<>(200, "Create lesson successfully", response);

        } catch (Exception e) {
            // If database save fails, delete the uploaded file from Cloudinary
            if (cloudinaryUrl != null) {
                try {
                    cloudinaryService.deleteFile(cloudinaryUrl);
                } catch (Exception deleteException) {
                    // Log the deletion error but don't throw it
                    System.err.println("Failed to delete uploaded file from Cloudinary: " + deleteException.getMessage());
                }
            }
            throw new CustomServiceException("Failed to create lesson: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Object> editLesson(LessonRequest lessonRequest, UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }

        // Find lesson
        Optional<Lessons> lessonOptional;
        if (role.equals(Role.ADMIN)) {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndIsActiveTrue(lessonId, courseId);
        } else {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId);
        }

        if (lessonOptional.isEmpty()) {
            throw new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND);
        }

        Lessons lesson = lessonOptional.get();

        // Check if title is being changed and conflicts with existing lesson
        if (!lesson.getTitle().equals(lessonRequest.getTitle()) && lessonRepository.existsByCourseIdAndTitleAndIdNotAndIsActiveTrue(courseId, lessonRequest.getTitle(), lessonId)) {
            throw new CustomServiceException("Lesson with this title already exists in the course", HttpStatus.BAD_REQUEST);
        }

        if (lessonRequest.getTitle() != null) {
            lesson.setTitle(lessonRequest.getTitle());
        }
        if (lessonRequest.getUrl() != null) {
            lesson.setUrl(lessonRequest.getUrl());
        }
        if (lessonRequest.getType() != null) {
            lesson.setLessonType(lessonRequest.getType());
        }
        if (lessonRequest.getOrderIndex() != null) {
            lesson.setOrderIndex(lessonRequest.getOrderIndex());
        }
        lesson.setUpdatedBy(user);
        lesson.setUpdatedAt(new Date());

        Lessons updatedLesson = lessonRepository.save(lesson);

        // Convert to response using static method
        LessonResponse response = LessonResponse.convertToResponse(updatedLesson);
        return new ApiResponse<>(200, "Update lesson successfully", response);
    }

    @Override
    public ApiResponse<List<LessonResponse>> searchLessonsByTitleAndCourse(String title, UUID courseId) {
        try {
            List<Lessons> lessons = lessonRepository.searchLessonsByTitleAndCourse(title, courseId);
            List<LessonResponse> responseList = lessons.stream().map(LessonResponse::convertToResponse).collect(Collectors.toList());

            String message = lessons.isEmpty() ? "No lessons found for this course" : "Course lessons retrieved successfully";
            return new ApiResponse<>(200, message, responseList);
        } catch (Exception e) {
            throw new CustomServiceException("Error searching course lessons: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ApiResponse<Object> hideLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }

        // Find lesson
        Optional<Lessons> lessonOptional;
        if (role.equals(Role.ADMIN)) {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndIsActiveTrue(lessonId, courseId);
        } else {
            lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId);
        }

        if (lessonOptional.isEmpty()) {
            throw new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND);
        }

        Lessons lesson = lessonOptional.get();
        lesson.setIsActive(false);
        lesson.setUpdatedBy(user);
        lesson.setUpdatedAt(new Date());

        Lessons hiddenLesson = lessonRepository.save(lesson);

        // Convert to response using static method
        LessonResponse response = LessonResponse.convertToResponse(hiddenLesson);
        return new ApiResponse<>(200, "Hide lesson successfully", response);
    }

    @Override
    public ApiResponse<Object> deleteLesson(UUID courseId, UUID lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Only admin can hard delete
        if (!checkRole(authentication).equals(Role.ADMIN)) {
            throw new CustomServiceException("Only admin can delete lesson", HttpStatus.FORBIDDEN);
        }

        Optional<Lessons> lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId);
        if (lesson.isEmpty()) {
            throw new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND);
        }

        lessonRepository.delete(lesson.get());
        return new ApiResponse<>(200, "Delete lesson successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<BatchUpdateResponse> updateLessonBatch(UpdateLessonBatchRequest request, UUID courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role and permissions
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This user is not an instructor", HttpStatus.FORBIDDEN);
        }

        List<BatchUpdateResponse.BatchUpdateError> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // Validate and update each lesson
        for (UpdateLessonBatchRequest.UpdateLessonItem item : request.getLessons()) {
            try {
                // Parse lesson ID from string to UUID
                UUID lessonId = UUID.fromString(item.getId());

                // Find lesson with permission check
                Optional<Lessons> lessonOptional;
                if (role.equals(Role.ADMIN)) {
                    lessonOptional = lessonRepository.findLessonEntityByIdAndCourseId(lessonId, courseId);
                } else {
                    lessonOptional = lessonRepository.findLessonEntityByIdAndCourseIdAndInstructorId(lessonId, courseId, currentUserId);
                }

                // Check if lesson exists and user has permission
                if (lessonOptional.isEmpty()) {
                    errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Lesson not found or no permission"));
                    failureCount++;
                    continue;
                }

                Lessons lesson = lessonOptional.get();
                boolean hasChanges = false;

                // Update orderIndex if provided
                if (item.getOrderIndex() != null && !item.getOrderIndex().equals(lesson.getOrderIndex())) {
                    // Check for duplicate orderIndex within the current batch request
                    boolean hasDuplicateInBatch = request.getLessons().stream().filter(other -> other != item && other.getOrderIndex() != null).anyMatch(other -> other.getOrderIndex().equals(item.getOrderIndex()));

                    if (hasDuplicateInBatch) {
                        errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Duplicate orderIndex in batch"));
                        failureCount++;
                        continue;
                    }

                    // Check for duplicate orderIndex in the database
                    boolean existsInDb = lessonRepository.existsByCourseIdAndOrderIndexAndIdNot(courseId, item.getOrderIndex(), lessonId);
                    if (existsInDb) {
                        errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Order index already exists in course"));
                        failureCount++;
                        continue;
                    }

                    lesson.setOrderIndex(item.getOrderIndex());
                    hasChanges = true;
                }

                // Update isActive if provided
                if (item.getIsActive() != null && !item.getIsActive().equals(lesson.getIsActive())) {
                    lesson.setIsActive(item.getIsActive());
                    hasChanges = true;
                }

                // Save changes if any modifications were made
                if (hasChanges) {
                    lesson.setUpdatedBy(user);
                    lesson.setUpdatedAt(new Date());
                    lessonRepository.save(lesson);
                    successCount++;
                } else {
                    // Count as success even if no changes were made
                    successCount++;
                }

            } catch (IllegalArgumentException e) {
                errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Invalid lesson ID format"));
                failureCount++;
            } catch (Exception e) {
                errors.add(new BatchUpdateResponse.BatchUpdateError(item.getId(), "Internal server error: " + e.getMessage()));
                failureCount++;
            }
        }

        // Create success response with BatchUpdateResponse
        String message = String.format("Batch update completed. Success: %d, Failure: %d", successCount, failureCount);
        BatchUpdateResponse batchResponse = new BatchUpdateResponse(message, 200, successCount, failureCount, errors);
        return new ApiResponse<>(200, "Batch update processed successfully", batchResponse);
    }
}
