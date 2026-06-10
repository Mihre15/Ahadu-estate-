package com.Ahadu_backend.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}