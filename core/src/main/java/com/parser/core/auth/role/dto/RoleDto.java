package com.parser.core.auth.role.dto;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.validation.ChainValidator;
import com.parser.core.util.validation.Validatable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDto implements Serializable, Validatable, IdBased {

    UUID id;
    String name;
    String description;
    UUID createdBy;
    LocalDateTime cdt;
    List<PermissionDto> permissions;

    @Override
    public String validateMessage() {
        return ChainValidator.create()
                .thenNotEmpty(this::getName, "Название роли не указано")
                .then(() -> {
                    var permissions = getPermissions();
                    if (permissions == null || permissions.isEmpty()) return "Не указано ни одного разрешения для роли";
                    return permissions.stream()
                            .map(perm -> {
                                var validateId = Validatable.validateId(perm.getId());
                                if (validateId == null) {
                                    return perm.canSomething() ? null : "Не указано ни одного типа доступа для разрешения";
                                } else {
                                    return validateId;
                                }
                            })
                            .filter(Objects::nonNull)
                            .findAny().orElse(null);
                })
                .validateMessage();
    }
}
