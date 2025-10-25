package com.example.server.controller;

import com.example.server.dto.CoursesDto;
import com.example.server.request.CoursesRequest;
import com.example.server.response.MessageResponse;
import com.example.server.service.CoursesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {

    @Autowired
    private CoursesService coursesService;

    @GetMapping()
    public ResponseEntity<?> getListCourses(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(coursesService.getPageCourses(page, size).getContent());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable UUID courseId) {
        CoursesDto coursesDto = coursesService.getCourse(courseId);
        return ResponseEntity.ok(
                coursesDto == null ? new MessageResponse("No data", 404) : coursesDto
        );
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody @Valid CoursesRequest coursesRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(coursesService.createCourse(coursesRequest));
    }

    @PutMapping("/edit/{courseId}")
    public ResponseEntity<?> editCourse(@PathVariable UUID courseId, @RequestBody @Valid CoursesRequest coursesRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new MessageResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), 400), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(coursesService.editCourse(coursesRequest, courseId));
    }

    @PutMapping("/hidden/{courseId}")
    public ResponseEntity<?> editCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.hiddenCourse(courseId));
    }
}
