package com.parser.core.admin.repo;

import com.parser.core.admin.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode, UUID> {
    Optional<VerificationCode> findByUserIdAndEmail(UUID userId, String email);

    Optional<VerificationCode> findByEmail(String email);

    @Modifying
    @Transactional
    void deleteAllByEmail(String email);

    @Modifying
    @Transactional
    void deleteAllByUserId(UUID userId);
} 