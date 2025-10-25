package com.example.server.repository;

import com.example.server.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

    Boolean existsUsersByEmail(String email);

    Users findByEmailAndIsActive(String email, Boolean isActive);

    Users findByIdAndIsActive(UUID id, Boolean isActive);
}
