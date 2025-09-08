package com.parser.core.auth.core.service;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.entity.AuthRole;
import com.parser.core.auth.core.repo.AuthRoleRepo;
import com.parser.core.exceptions.ForbiddenException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthRoleServiceImpl implements AuthRoleService {

    AuthRoleRepo repo;

    @Override
    @Transactional
    public void save(AdminEntity user, Set<AuthRole> authRoles) {
        repo.deleteAllByAuth(user);
        repo.saveAll(authRoles);
    }

    @Override
    public void updateActive(AdminEntity user, Map<UUID, Boolean> roles) {
        Set<AuthRole> authRoles = roles.entrySet().stream()
                .map(entry -> {
                    AuthRole authRole = repo.findAllByAuthIdAndRoleId(user.getId(), entry.getKey())
                            .orElseThrow(ForbiddenException::new);
                    authRole.setActive(entry.getValue());
                    return authRole;
                })
                .collect(Collectors.toSet());

        repo.saveAll(authRoles);
    }
}
