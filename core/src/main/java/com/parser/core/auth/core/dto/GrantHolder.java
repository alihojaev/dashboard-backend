package com.parser.core.auth.core.dto;

import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.AccessPermission;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

@Value
public class GrantHolder implements GrantedAuthority, AccessPermission {

    PermissionType permission;
    int access;

    @Override
    public int getAccess() {
        return access;
    }

    @Override
    public String getAuthority() {
        return permission.name();
    }
}