package com.parser.server.config;

import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.common.dict.BaseDict;
import com.parser.core.common.dto.BaseDictDto;
import com.parser.core.common.service.BaseDictService;
import com.parser.core.config.permission.annotation.ManualPermissionControl;
import com.parser.server.config.grant.GrantService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class BaseDictController<E extends BaseDict, M extends BaseDictDto, S extends BaseDictService<E, M>> {

    S service;
    GrantService grant;
    PermissionType controllerPermission;
    EnumSet<PermissionType> canRead;

    public BaseDictController(S service, GrantService grant, PermissionType controllerPermission, PermissionType... canRead) {
        this.service = service;
        this.grant = grant;
        this.controllerPermission = controllerPermission;
        this.canRead = canRead != null && canRead.length > 0 ? EnumSet.copyOf(Arrays.asList(canRead)) : EnumSet.noneOf(PermissionType.class);
        this.canRead.add(controllerPermission);
    }

    @GetMapping
    @ManualPermissionControl
    public List<M> listAll() {
        grant.checkHasAny(AccessType.READ, canRead);
        return service.listAllAsModel();
    }

    @PostMapping
    @ManualPermissionControl
    public UUID save(@RequestBody M model) {
        if (model.getId() == null) {
            grant.checkHasAny(controllerPermission, AccessType.CREATE);
        } else {
            grant.checkHasAny(controllerPermission, AccessType.UPDATE);
        }

        return service.save(model);
    }

    @DeleteMapping
    @ManualPermissionControl
    public void delete(@RequestParam UUID dictId) {
        grant.checkHasAny(controllerPermission, AccessType.DELETE);
        service.delete(dictId);
    }
}
