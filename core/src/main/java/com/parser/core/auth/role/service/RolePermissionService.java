package com.parser.core.auth.role.service;

import com.parser.core.auth.role.entity.RolePermission;

import java.util.List;

public interface RolePermissionService {

    void saveAll(List<RolePermission> rolePermissions);

    void deleteAll(List<RolePermission> rolePermissions);
}
