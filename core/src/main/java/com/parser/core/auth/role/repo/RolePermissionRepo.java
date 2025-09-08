package com.parser.core.auth.role.repo;

import com.parser.core.auth.role.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepo extends JpaRepository<RolePermission, Long> {
}
