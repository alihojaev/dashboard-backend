package com.parser.server.controllers;

import com.parser.core.admin.AdminCrateDto;
import com.parser.core.admin.AdminDto;
import com.parser.core.admin.AdminUpdatePasswordDto;
import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.admin.service.AdminService;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.auth.role.service.RoleService;
import com.parser.core.config.permission.annotation.HasAccess;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.config.permission.annotation.ManualPermissionControl;
import com.parser.core.exceptions.ForbiddenException;
import com.parser.core.util.rsql.RsqlSpecificationFactory;
import com.parser.server.config.backend.BackendTokenContextHolder;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
@HasPermission(PermissionType.ADMINS)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {

    AdminService service;
    RoleService roleService;

    @GetMapping
    @HasAccess(AccessType.READ)
    public List<AdminDto> getUsers(@Parameter(hidden = true) @PageableDefault Pageable pageable) {
        return service.getUsers(pageable);
    }

    @GetMapping("/byId")
    @HasAccess(AccessType.READ)
    public AdminDto getUserById(@RequestParam UUID id) {
        return service.geById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAccess(AccessType.READ)
    public ResponseEntity<?> list(String query, Pageable pageable) {
        RsqlSpecificationFactory<AdminEntity> factory = new RsqlSpecificationFactory<>();
        Specification<AdminEntity> spec = factory.createSpecification(query);
        return ResponseEntity.ok(service.searchPageable(spec, pageable));
    }


    @PostMapping
    @HasAccess(AccessType.READ)
    public UUID save(@RequestBody AdminCrateDto model) {
        return service.save(model, BackendTokenContextHolder.current().getPrincipal().getId()).getId();
    }

    @PutMapping("/password")
    @HasAccess(AccessType.READ)
    public void updatePassword(@RequestBody AdminUpdatePasswordDto model) {
        service.updatePassword(model, BackendTokenContextHolder.current().getPrincipal().getId());
    }

    @PostMapping("/block")
    @HasAccess(AccessType.READ)
    public void block(@RequestParam UUID id) {
        service.block(id);
    }

    @DeleteMapping()
    @HasAccess(AccessType.DELETE)
    public void delete(@RequestParam UUID id) {
        service.delete(id);
    }

    @ManualPermissionControl
    @GetMapping("/current")
    public AdminDto current() {
        return BackendTokenContextHolder.currentOptional().map(
                context -> {
                    AdminEntity auth = context.getPrincipal();
                    return service.toModel(auth);
                }
        ).orElseThrow(ForbiddenException::new);
    }
}
