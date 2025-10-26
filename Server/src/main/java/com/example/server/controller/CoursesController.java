package com.example.server.controller;

import com.example.server.request.CoursesRequest;
import com.example.server.service.CoursesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return ResponseEntity.ok(coursesService.getCourse(courseId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCourse(@RequestBody @Valid CoursesRequest coursesRequest) {
        return ResponseEntity.ok(coursesService.createCourse(coursesRequest));
    }

    @PutMapping("/edit/{courseId}")
    public ResponseEntity<?> editCourse(@PathVariable UUID courseId, @RequestBody @Valid CoursesRequest coursesRequest) {
        return ResponseEntity.ok(coursesService.editCourse(coursesRequest, courseId));
    }

    @PutMapping("/hidden/{courseId}")
    public ResponseEntity<?> editCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.hiddenCourse(courseId));
    }

    @GetMapping("/{courseId}/student-of-course")
    public ResponseEntity<?> getPageStudentsOfCourse(@RequestParam int page, @RequestParam int size, @PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.getPageStudentsOfCourse(page, size, courseId).getContent());
    }

    @GetMapping("/{courseId}/student-not-course")
    public ResponseEntity<?> getPageStudentsNotCourse(@RequestParam int page, @RequestParam int size, @PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.getPageStudentsNotCourse(page, size, courseId).getContent());
    }

    @PostMapping("/{courseId}/add-student-to-course")
    public ResponseEntity<?> addStudentToCourse(@PathVariable UUID courseId, @RequestParam List<UUID> studentsId) {
        return ResponseEntity.ok(coursesService.addStudentToCourse(courseId, studentsId));
    }
}
