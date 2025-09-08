package com.parser.core.auth.core.service;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.repo.AuthRepo;
import com.parser.core.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    AuthRepo repo;

    @Override
    public Optional<AdminEntity> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public AdminEntity findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new BadRequestException("auth not found"));
    }

    @Override
    public void save(AdminEntity user) {
        repo.save(user);
    }

    @Override
    public Optional<String> findUsernameById(UUID authId) {
        return repo.findById(authId).map(AdminEntity::getUsername);
    }

    @Override
    public void updateLastActivity(UUID authId) {
        var auth = findById(authId);
        auth.setLastActivity(LocalDateTime.now());
        repo.save(auth);
    }
}
