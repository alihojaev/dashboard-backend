package com.parser.core.auth.role.mapper;

import com.parser.core.auth.core.entity.AuthRoleId;
import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.auth.role.entity.RolePermissionId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Mapper
@Component
public interface RoleMapper {

    @Mapping(target = "rolePermissions", source = "dto.permissions")
    Role toEntity(RoleDto dto);

    List<RoleDto> toDtoList(List<Role> entity);

    @Mapping(target = "permissions", source = "entity.rolePermissions")
    RoleDto toDto(Role entity);

    void updateDto(@MappingTarget Role entity, RoleDto dto);

    List<PermissionDto> mapRolePermissions(List<RolePermission> rolePermissions);

    default PermissionDto mapRolePermission(RolePermission rolePermission) {
        if (rolePermission == null) {
            return null;
        }
        return new PermissionDto(
                rolePermission.getPermission().getId(),
                rolePermission.getPermission().getName(),
                rolePermission.getPermissionAccess()
        );
    }

    default RolePermissionId mapRolePermissionId(UUID value) {
        if (value == null) {
            return null;
        }
        RolePermissionId rolePermissionId = new RolePermissionId();
        rolePermissionId.setRoleId(value);
        return rolePermissionId;
    }

    default AuthRoleId mapAuthRoleId(UUID value) {
        if (value == null) {
            return null;
        }
        AuthRoleId authRoleId = new AuthRoleId();
        authRoleId.setRoleId(value);
        return authRoleId;
    }

    default UUID map(String value) {
        if (value == null) return null;
        return UUID.fromString(value);
    }

    default String map(UUID value) {
        if (value == null) return null;
        return value.toString();
    }

}
