package com.parser.core.admin.mapper;

import com.parser.core.admin.AdminCrateDto;
import com.parser.core.admin.AdminDto;
import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.entity.AuthRole;
import com.parser.core.auth.core.entity.AuthRoleId;
import com.parser.core.auth.role.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Mapper
@Component
public interface AdminMapper {

    AdminEntity toEntity(AdminDto dto);

    @Mapping(target = "authRoles", ignore = true)
    AdminEntity toCreateEntity(AdminCrateDto dto);

    @Mapping(target = "authRoles", source = "entity.authRoles")
    AdminDto toDto(AdminEntity entity);

    List<AdminDto> toDtoList(List<AdminEntity> entity);

    void updateDto(@MappingTarget AdminEntity entity, AdminDto dto);

    default AuthRoleId map(UUID value) {
        return new AuthRoleId(null, value);
    }

    default UUID map(AuthRoleId value) {
        return value == null ? null : value.getRoleId();
    }

    default RoleDto authRoleToRoleDto(AuthRole authRole) {
        if (authRole == null) {
            return null;
        }

        RoleDto roleDto = new RoleDto();

        roleDto.setId(map(authRole.getId()));
        roleDto.setName(authRole.getRole().getName());
        roleDto.setDescription(authRole.getRole().getDescription());

        return roleDto;
    }


}
