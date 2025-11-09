package com.example.server.controller;

import com.example.server.request.CoursesRequest;
import com.example.server.service.CoursesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {

    @Autowired
    private CoursesService coursesService;

    @GetMapping()
    public ResponseEntity<?> getListCourses(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(coursesService.getPageCourses(page, size));
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

    @GetMapping("/{courseId}/student-in-course")
    public ResponseEntity<?> getPageStudentsInCourse(@RequestParam int page, @RequestParam int size, @PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.getPageStudentsInCourse(page, size, courseId));
    }

    @GetMapping("/{courseId}/student-not-course")
    public ResponseEntity<?> getPageStudentsNotCourse(@RequestParam int page, @RequestParam int size, @PathVariable UUID courseId) {
        return ResponseEntity.ok(coursesService.getPageStudentsNotCourse(page, size, courseId));
    }

    @PostMapping("/{courseId}/add-student-to-course")
    public ResponseEntity<?> addStudentToCourse(@PathVariable UUID courseId, @RequestParam List<UUID> studentIds) {
        return ResponseEntity.ok(coursesService.addStudentToCourse(courseId, studentIds));
    }

    @PutMapping("/{courseId}/remove-student-from-course/{studentId}")
    public ResponseEntity<?> removeStudentFromCourse(@PathVariable UUID courseId, @PathVariable UUID studentId) {
        return ResponseEntity.ok(coursesService.removeStudentFromCourse(courseId, studentId));
    }
}
