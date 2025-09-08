package com.parser.core.auth.permission.dto;

import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.AccessPermission;
import lombok.Value;

import java.util.UUID;

@Value
public class PermissionDto implements AccessPermission {

    UUID id;
    PermissionType name;
    int operationPermissions;

    @Override
    public int getAccess() {
        return operationPermissions;
    }

    public Permission fromModel() {
        return new Permission(getId());
    }
}
