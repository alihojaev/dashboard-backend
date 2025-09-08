package com.parser.core.auth.core.dto;

import com.parser.core.auth.role.dto.RoleDto;
import com.parser.core.common.entity.base.IdBased;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthDto implements IdBased {

    UUID id;
    Set<RoleDto> roles;
}
