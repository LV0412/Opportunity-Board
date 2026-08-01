package com.opportunityboard.repository;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailVerificationToken(String emailVerificationToken);

    boolean existsByEmail(String email);

    long countByRole(UserRole role);
}
