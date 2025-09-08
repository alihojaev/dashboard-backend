package com.parser.core.auth.core.service;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.entity.AuthRole;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface AuthRoleService {

    void save(AdminEntity user, Set<AuthRole> authRoles);

    void updateActive(AdminEntity user, Map<UUID, Boolean> role);
}
