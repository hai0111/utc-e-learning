package com.example.server.service.impl;

import com.example.server.dto.CoursesDto;
import com.example.server.repository.CoursesRepository;
import com.example.server.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Override
    public Page<CoursesDto> getPageCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return coursesRepository.findActiveCoursesWithAllRelations(pageable, true);
    }
}
