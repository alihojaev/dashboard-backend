package com.parser.core.auth.permission.repo;

import com.parser.core.auth.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepo extends JpaRepository<Permission, Long> {
}
