package com.parser.core.auth.permission.jdbc;


import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.common.jdbc.Dao;

import java.util.List;
import java.util.UUID;

public interface PermissionDao extends Dao<Permission> {

    List<PermissionDto> listAll();

    List<RolePermission> listAllByRoleId(UUID roleId);

    List<PermissionDto> listAllByRoleIdAsModel(UUID roleId);
}
