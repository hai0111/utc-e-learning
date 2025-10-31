package com.example.server.controller;

import com.example.server.request.LessonRequest;
import com.example.server.request.UpdateLessonBatchRequest;
import com.example.server.response.BatchUpdateResponse;
import com.example.server.response.LessonResponse;
import com.example.server.response.MessageResponse;
import com.example.server.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
@RequiredArgsConstructor
public class LesssonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping()
    public ResponseEntity<?> getListLessons(@PathVariable UUID courseId) {
        List<LessonResponse> lessons = lessonService.getListLessons(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        LessonResponse lessonResponse = lessonService.getLesson(courseId, lessonId);
        return ResponseEntity.ok(
                lessonResponse == null ? new MessageResponse("No data", 404) : lessonResponse
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@PathVariable UUID courseId, @RequestBody @Valid LessonRequest lessonRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(lessonService.createLesson(lessonRequest, courseId));
    }

    @PutMapping("/edit/{lessonId}")
    public ResponseEntity<?> editLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId, @RequestBody @Valid LessonRequest lessonRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(lessonService.editLesson(lessonRequest, courseId, lessonId));
    }

    @PutMapping("/hidden/{lessonId}")
    public ResponseEntity<?> hiddenLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        return ResponseEntity.ok(lessonService.hiddenLesson(courseId, lessonId));
    }

    @DeleteMapping("/delete/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable UUID courseId, @PathVariable UUID lessonId) {
        return ResponseEntity.ok(lessonService.deleteLesson(courseId, lessonId));
    }

    @PutMapping("/batch-update")
    public ResponseEntity<?> updateLessonBatch(
            @PathVariable UUID courseId,
            @RequestBody @Valid UpdateLessonBatchRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400),
                    HttpStatus.BAD_REQUEST
            );
        }

        BatchUpdateResponse response = lessonService.updateLessonBatch(request, courseId);
        return ResponseEntity.ok(response);
    }
}
