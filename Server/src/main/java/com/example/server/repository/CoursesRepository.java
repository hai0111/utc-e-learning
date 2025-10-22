package com.example.server.repository;

import com.example.server.dto.CoursesDto;
import com.example.server.model.Courses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, UUID> {

    @Query("SELECT c.id as id, " +
            "c.title as title, " +
            "c.description as description, " +
            "u.name as instructor " +
            "FROM Courses c " +
            "INNER JOIN Users u on c.users.id = u.id " +
            "WHERE c.isActive = :isActive ORDER BY c.updatedAt DESC")
    Page<CoursesDto> findActiveCoursesWithAllRelations(Pageable pageable, Boolean isActive);
}
