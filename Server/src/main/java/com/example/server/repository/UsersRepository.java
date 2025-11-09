package com.example.server.repository;

import com.example.server.dto.StudentDto;
import com.example.server.dto.UsersDto;
import com.example.server.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Boolean existsUsersByEmail(String email);

    Users findByEmailAndIsActive(String email, Boolean isActive);

    Users findByIdAndIsActive(UUID id, Boolean isActive);

    @Query("SELECT u.id as id, " +
            "u.code as code, " +
            "u.name as name, " +
            "u.email as email " +
            "FROM Users u " +
            "WHERE u.role = 'STUDENT' AND u.isActive = true " +
            "AND u.id IN (" +
            "   SELECT DISTINCT e.users.id " +
            "   FROM Enrollment e " +
            "   WHERE e.course.id = :courseId AND e.isActive = true" +
            ")")
    Page<StudentDto> findAllStudentsOfCourse(UUID courseId, Pageable pageable);

    @Query("SELECT u.id " +
            "FROM Users u " +
            "WHERE u.role = 'STUDENT' AND u.isActive = true " +
            "AND u.id IN (" +
            "   SELECT DISTINCT e.users.id " +
            "   FROM Enrollment e " +
            "   WHERE e.course.id = :courseId AND e.isActive = true" +
            ")")
    List<UUID> findAllStudentIdsOfCourse(UUID courseId);

    @Query("SELECT COUNT(u.id) " +
            "FROM Users u " +
            "WHERE u.role = 'STUDENT' AND u.isActive = true " +
            "AND u.id IN (" +
            "   SELECT DISTINCT e.users.id " +
            "   FROM Enrollment e " +
            "   WHERE e.course.id = :courseId AND e.isActive = true" +
            ")")
    Long totalStudentsInCourse(UUID courseId);

    @Query("SELECT u.id as id, " +
            "u.code as code, " +
            "u.name as name, " +
            "u.email as email " +
            "FROM Users u " +
            "WHERE u.role = 'STUDENT' AND u.isActive = true " +
            "AND u.id NOT IN (" +
            "   SELECT DISTINCT e.users.id " +
            "   FROM Enrollment e " +
            "   WHERE e.course.id = :courseId AND e.isActive = true" +
            ")")
    Page<StudentDto> findAllStudentsNotCourse(UUID courseId, UUID userId, Pageable pageable);

    @Query("SELECT u.id " +
            "FROM Users u " +
            "WHERE u.role = 'STUDENT' AND u.isActive = true ")
    List<UUID> findAllStudentsId();

    @Query("SELECT u.id as id, " +
            "u.name as name, " +
            "u.code as code, " +
            "u.email as email, " +
            "u.role as role, " +
            "u.isActive as isActive," +
            "u.createdBy as createdBy," +
            "u.updatedBy as updatedBy " +
            "FROM Users u " +
            "INNER JOIN Courses c on c.users.id = u.id " +
            "WHERE c.id = :courseId AND c.users.id = :userId and c.isActive = true and u.isActive = true")
    UsersDto findByCourseAndInstructorId(UUID courseId, UUID userId);
}
