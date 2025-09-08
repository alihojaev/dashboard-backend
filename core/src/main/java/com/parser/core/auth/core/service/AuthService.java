package com.parser.core.auth.core.service;

import com.parser.core.admin.entity.AdminEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthService {

    Optional<AdminEntity> findByUsername(String username);

    AdminEntity findById(UUID id);

    void save(AdminEntity user);

    Optional<String> findUsernameById(UUID authId);

    void updateLastActivity(UUID authId);

}
