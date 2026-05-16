package com.fintrack.FinTrackV2.repository;

import com.fintrack.FinTrackV2.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(
            String email);
}