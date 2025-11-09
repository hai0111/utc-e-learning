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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final UsersRepository usersRepository;
    private final CoursesRepository courseRepository;
    private final CloudinaryService cloudinaryService;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

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


    @Override
    public List<LessonResponse> getListLessons(UUID courseId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        return switch (role) {
            case ADMIN -> lessonRepository.findByCourseIdAndIsActiveTrueOrderByOrderIndexAsc(courseId);
            case INSTRUCTOR ->
                    lessonRepository.findByCourseIdAndInstructorIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
            case STUDENT ->
                    lessonRepository.findByCourseIdAndStudentIdAndIsActiveTrueOrderByOrderIndexAsc(courseId, currentUserId);
            default -> throw new RuntimeException("Invalid role");
        };
    }

    @Override
    public ApiResponse<LessonResponse> getLesson(UUID courseId, UUID lessonId) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UUID currentUserId = userDetails.getId();
            Role role = checkRole(authentication);

            LessonResponse lessonResponse;
            switch (role) {
                case ADMIN:
                    lessonResponse = lessonRepository.findByIdAndCourseIdAndIsActiveTrue(lessonId, courseId)
                            .orElse(null);
                    break;
                case INSTRUCTOR:
                    lessonResponse = lessonRepository.findByIdAndCourseIdAndInstructorIdAndIsActiveTrue(lessonId, courseId, currentUserId)
                            .orElse(null);
                    break;
                case STUDENT:
                    lessonResponse = lessonRepository.findByIdAndCourseIdAndStudentIdAndIsActiveTrue(lessonId, courseId, currentUserId)
                            .orElse(null);
                    break;
                default:
                    return new ApiResponse<>(403, "Invalid role",null);
            }

            if (lessonResponse == null) {
                return new ApiResponse<>(404, "Lesson not found",null);
            }

            return new ApiResponse<>(200,"Get lesson successfully", lessonResponse);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Internal server error: " + e.getMessage(),null);
        }
    }

    @Override
    public ApiResponse<Object> createLesson(LessonRequest lessonRequest, UUID courseId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            return new ApiResponse<>(400, "User does not exist",null);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            return new ApiResponse<>(403, "This is not an instructor",null);
        }

        // Check if course exists
        Optional<Courses> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            return new ApiResponse<>(404, "Course not found",null);
        }

        // Check permission for instructor
        if (role.equals(Role.INSTRUCTOR)) {
            if (!course.get().getUsers().getId().equals(currentUserId)) {
                return new ApiResponse<>(403, "You don't have permission to create lesson in this course",null);
            }
        }

        // Check if lesson with same title already exists
        if (lessonRepository.existsByCourseIdAndTitleAndIsActiveTrue(courseId, lessonRequest.getTitle())) {
            return new ApiResponse<>(400, "Lesson with this title already exists in the course",null);
        }

        // Validate file
        MultipartFile file = lessonRequest.getFile();
        if (file == null || file.isEmpty()) {
            return new ApiResponse<>(400, "File is required",null);
        }

        // Validate file size (e.g., max 1024MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            return new ApiResponse<>(400, "File size must be less than 100MB",null);
        }

        String cloudinaryUrl = null;
        try {
            // Upload file to Cloudinary
            String folderName = "course-" + courseId.toString();
            cloudinaryUrl = cloudinaryService.uploadFile(file, folderName);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to upload file: " + e.getMessage(),null);
        }

        try {
            // Get the maximum orderIndex for this course and increment by 1
            Integer maxOrderIndex = lessonRepository.findMaxOrderIndexByCourseId(courseId);
            Integer newOrderIndex = maxOrderIndex + 1;

            // Create lesson entity
            Lessons lesson = new Lessons();
            lesson.setCourse(course.get());
            lesson.setTitle(lessonRequest.getTitle());
            lesson.setUrl(cloudinaryUrl); // Save Cloudinary URL
            lesson.setLessonType(lessonRequest.getType());
            lesson.setOrderIndex(newOrderIndex); // Auto-increment orderIndex
            lesson.setIsActive(true);
            lesson.setCreatedBy(user);
            lesson.setUpdatedBy(user);
            lesson.setCreatedAt(new Date());
            lesson.setUpdatedAt(new Date());

            Lessons savedLesson = lessonRepository.save(lesson);

            // Return success response with detailed information
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("lessonId", savedLesson.getId());
            responseData.put("fileUrl", cloudinaryUrl);
            responseData.put("title", savedLesson.getTitle());
            responseData.put("orderIndex", newOrderIndex);
            responseData.put("createdBy", user.getName()); // Creator name
            responseData.put("createdById", user.getId()); // Creator ID
            responseData.put("createdAt", savedLesson.getCreatedAt()); // Creation date
            responseData.put("type", savedLesson.getLessonType());
            responseData.put("courseId", courseId);

            return new ApiResponse<>(200,"Create lesson successfully", responseData);

        } catch (Exception e) {
            // If database save fails, try to delete the uploaded file from Cloudinary
            if (cloudinaryUrl != null) {
                try {
                    cloudinaryService.deleteFile(cloudinaryUrl);
                } catch (Exception deleteException) {
                    // Log the deletion error but don't throw it
                    System.err.println("Failed to delete uploaded file from Cloudinary: " + deleteException.getMessage());
                }
            }
            return new ApiResponse<>(500, "Failed to create lesson: " + e.getMessage(),null);
        }
    }

    @Override
    public ApiResponse<Object> editLesson(LessonRequest lessonRequest, UUID courseId, UUID lessonId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This is not an instructor", HttpStatus.FORBIDDEN);
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
        if (!lesson.getTitle().equals(lessonRequest.getTitle()) &&
                lessonRepository.existsByCourseIdAndTitleAndIdNotAndIsActiveTrue(courseId, lessonRequest.getTitle(), lessonId)) {
            throw new CustomServiceException("Lesson with this title already exists in the course", HttpStatus.BAD_REQUEST);
        }

        lesson.setTitle(lessonRequest.getTitle());
        lesson.setUrl(lessonRequest.getUrl());
        lesson.setLessonType(lessonRequest.getType());
        if (lessonRequest.getOrderIndex() != null) {
            lesson.setOrderIndex(lessonRequest.getOrderIndex());
        }
        lesson.setUpdatedBy(user);
        lesson.setUpdatedAt(new Date());

        Lessons lessons = lessonRepository.save(lesson);
        return new ApiResponse<>(200,"Update lesson successfully",lessons);
    }

    @Override
    public ApiResponse<List<LessonResponse>> searchLessonsByTitleAndCourse(String title, UUID courseId) {
        try {
            List<Lessons> lessons = lessonRepository.searchLessonsByTitleAndCourse(title, courseId);
            List<LessonResponse> responseList = lessons.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>(
                    200,
                    lessons.isEmpty() ? "No lessons found for this course" : "Course lessons retrieved successfully",
                    responseList
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    500,
                    "Error searching course lessons: " + e.getMessage(),
                    null
            );
        }
    }


    @Override
    public ApiResponse<Object> hiddenLesson(UUID courseId, UUID lessonId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            throw new CustomServiceException("User does not exist", HttpStatus.BAD_REQUEST);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            throw new CustomServiceException("This is not an instructor", HttpStatus.FORBIDDEN);
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
        Lessons lessons = lessonRepository.save(lesson);

        return new ApiResponse<>(200,"Hide lesson successfully",lessons);
    }

    @Override
    public ApiResponse<Object> deleteLesson(UUID courseId, UUID lessonId) {
        // Only admin can hard delete
        if (!checkRole(authentication).equals(Role.ADMIN)) {
            throw new CustomServiceException("Only admin can delete lesson", HttpStatus.FORBIDDEN);
        }

        Optional<Lessons> lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId);
        if (lesson.isEmpty()) {
            throw new CustomServiceException("Lesson not found", HttpStatus.NOT_FOUND);
        }

        lessonRepository.delete(lesson.get());
        return new ApiResponse<>(200,"Delete lesson successfully",null);
    }

    @Override
    @Transactional
    public ApiResponse<BatchUpdateResponse> updateLessonBatch(UpdateLessonBatchRequest request, UUID courseId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID currentUserId = userDetails.getId();
        Role role = checkRole(authentication);

        // Check user role and permissions
        Users user = usersRepository.findByIdAndIsActive(currentUserId, true);
        if (user == null) {
            BatchUpdateResponse errorResponse = new BatchUpdateResponse("User does not exist", 400, 0, 0, List.of());
            return new ApiResponse<>(400, "User does not exist", errorResponse);
        }
        if (user.getRole().equals(Role.STUDENT)) {
            BatchUpdateResponse errorResponse = new BatchUpdateResponse("This is not an instructor", 403, 0, 0, List.of());
            return new ApiResponse<>(403, "This is not an instructor", errorResponse);
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
                    boolean hasDuplicateInBatch = request.getLessons().stream()
                            .filter(other -> other != item && other.getOrderIndex() != null)
                            .anyMatch(other -> other.getOrderIndex().equals(item.getOrderIndex()));

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
        return new ApiResponse<>(200,"Batch update processed successfully", batchResponse);
    }

    private LessonResponse convertToResponse(Lessons lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setCourseId(lesson.getCourse().getId());
        response.setTitle(lesson.getTitle());
        response.setUrl(lesson.getUrl());
        response.setType(lesson.getLessonType());
        response.setIsActive(lesson.getIsActive());
        response.setOrderIndex(lesson.getOrderIndex());
        response.setCreatedAt(new Date());
        response.setUpdatedAt(new Date());

        // Set createdBy user info
        if (lesson.getCreatedBy() != null) {
            response.setCreatedBy(lesson.getCreatedBy().getId());
            response.setCreatedByName(lesson.getCreatedBy().getName()); // Assuming Users has getFullName()
        }

        // Set updatedBy user info
        if (lesson.getUpdatedBy() != null) {
            response.setUpdatedBy(lesson.getUpdatedBy().getId());
            response.setUpdatedByName(lesson.getUpdatedBy().getName());
        }

        return response;
    }
}
