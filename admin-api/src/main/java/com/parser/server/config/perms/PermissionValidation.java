package com.parser.server.config.perms;

import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.config.permission.ImmutableAccessPermission;
import com.parser.core.exceptions.BadRequestException;
import com.parser.core.exceptions.ForbiddenException;
import com.parser.server.config.backend.BackendTokenContextHolder;
import com.parser.server.config.grant.GrantServiceImpl;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;
import java.util.function.Predicate;


public class PermissionValidation {

    public static void validateCreateUpdate(IdBased idBased) {
        if (idBased == null) throw new BadRequestException("Id required");
        var context = BackendTokenContextHolder.currentOptional()
                .orElseThrow(ForbiddenException::new);
        var methodGrantHolder = context.getMethodGrantHolder();

        if (methodGrantHolder == null) throw new IllegalStateException(
                "Method have no permission annotations or marked with @ManualPermissionControl annotation"
        );

        Predicate<Map.Entry<PermissionType, ImmutableAccessPermission>> accessFilter;
        AccessType accessType;

        if (idBased.getId() == null) {
            accessFilter = entry -> entry.getValue().canCreate();
            accessType = AccessType.CREATE;
        } else {
            accessFilter = entry -> entry.getValue().canUpdate();
            accessType = AccessType.UPDATE;
        }

        var operationNotPermitted = methodGrantHolder.entrySet().stream()
                .filter(accessFilter)
                .map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue().getAccess() & accessType.getMask()))
                .noneMatch(entry -> GrantServiceImpl.getInstance()
                        .matchAll(
                                entry.getKey(),
                                entry.getValue()
                        )
                );

        if (operationNotPermitted) throw new ForbiddenException();
    }
}
