package com.parser.server.controllers;

import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.entityFields.dto.EntityFieldInfoDto;
import com.parser.core.entityFields.service.EntityFieldsService;
import com.parser.core.config.permission.annotation.HasAccess;
import com.parser.core.auth.role.enums.AccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entityFields")
@Slf4j
@AllArgsConstructor
@HasPermission(PermissionType.DASHBOARD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityFieldsController {
    
    private EntityFieldsService entityFieldsService;
    
    @GetMapping("/entities")
    @HasAccess(AccessType.READ)
    public ResponseEntity<List<String>> getAvailableEntities() {
        log.info("Получение списка доступных сущностей");
        List<String> entities = entityFieldsService.getAvailableEntities();
        return ResponseEntity.ok(entities);
    }
    
    @GetMapping("/entities/{entityName}/fields")
    @HasAccess(AccessType.READ)
    public ResponseEntity<List<EntityFieldInfoDto>> getEntityFields(@PathVariable String entityName) {
        log.info("Получение полей для сущности: {}", entityName);
        List<EntityFieldInfoDto> fields = entityFieldsService.getEntityFields(entityName);
        return ResponseEntity.ok(fields);
    }
} 