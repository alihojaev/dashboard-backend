package com.parser.core.auth.core.repo;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.entity.AuthRole;
import com.parser.core.auth.core.entity.AuthRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.UUID;

public interface AuthRoleRepo extends JpaRepository<AuthRole, AuthRoleId> {

    @Modifying
    void deleteAllByAuth(AdminEntity user);

    Optional<AuthRole> findAllByAuthIdAndRoleId(UUID authId, UUID roleId);
}
