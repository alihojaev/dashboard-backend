package com.parser.server.controllers;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.permission.service.PermissionService;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.annotation.HasAccess;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.util.rsql.RsqlSpecificationFactory;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/permission")
@HasPermission(PermissionType.ROLE)
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionsService;

    @GetMapping
    @HasAccess(AccessType.READ)
    public List<PermissionDto> list() {
        return permissionsService.listAllAsModel();
    }

}
