package com.parser.core.auth.core.repo;

import com.parser.core.admin.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepo extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findById(UUID id);

    Optional<AdminEntity> findByUsername(String username);

}
