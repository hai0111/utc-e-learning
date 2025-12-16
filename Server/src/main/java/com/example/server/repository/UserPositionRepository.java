package com.example.server.repository;

import com.example.server.model.UserPositions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserPositionRepository extends JpaRepository<UserPositions, UUID> {
    @Query("SELECT p.name FROM UserPositions up " +
            "JOIN up.positions p " +
            "WHERE up.users.id = :userId")
    List<String> findPositionNamesByUserId(@Param("userId") UUID userId);
}
