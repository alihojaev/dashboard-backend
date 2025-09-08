package com.parser.server.controllers;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.permission.service.PermissionService;
import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.auth.role.service.RoleService;
import com.parser.core.config.permission.annotation.HasAccess;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.entityFields.dto.EntityFieldInfoDto;
import com.parser.core.entityFields.service.EntityFieldsService;
import com.parser.core.util.rsql.RsqlSpecificationFactory;
import com.parser.server.config.perms.PermissionValidation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/role")
@HasPermission(PermissionType.ROLE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;
    EntityFieldsService entityFieldsService;
    PermissionService permissionService;

    @GetMapping
    @HasAccess(AccessType.READ)
    public List<RoleDto> list() {
        return roleService.listAllAsModel();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/rsql")
    @HasAccess(AccessType.READ)
    public ResponseEntity<?> listAll(String query, Pageable pageable) {
        RsqlSpecificationFactory<Role> factory = new RsqlSpecificationFactory<>();
        Specification<Role> spec = factory.createSpecification(query);
        return ResponseEntity.ok(roleService.searchPageable(spec, pageable));
    }

    @GetMapping("/fields")
    public List<EntityFieldInfoDto> getFields() {
        return entityFieldsService.getEntityFields("Role");
    }

    @GetMapping("/permissions")
    @HasAccess(AccessType.READ)
    public List<PermissionDto> listPermissions() {
        return permissionService.listAllAsModel();
    }

    @GetMapping("/unit")
    @HasAccess(AccessType.READ)
    public List<RoleDto> listRoleWithUnit() {
        return roleService.listAllByRdtIsNullAsModel();
    }

    @PostMapping
    @HasAccess({AccessType.CREATE, AccessType.UPDATE})
    public void save(@RequestBody RoleDto roleDto) {
        PermissionValidation.validateCreateUpdate(roleDto);
        roleService.save(roleDto);
    }

    @DeleteMapping()
    @HasAccess(AccessType.DELETE)
    public void delete(@RequestParam UUID roleId) {
        roleService.delete(roleId);
    }
}
