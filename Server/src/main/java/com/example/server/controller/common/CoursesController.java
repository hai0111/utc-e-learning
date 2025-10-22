package com.example.server.controller.common;

import com.example.server.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {

    @Autowired
    private CoursesService coursesService;

    @GetMapping()
    public ResponseEntity<?> getListCourses() {
        return ResponseEntity.ok(coursesService.getPageCourses(0, 3).getContent());
    }
}
