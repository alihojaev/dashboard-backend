package com.parser.core.admin;

import com.parser.core.auth.role.dto.RoleDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCrateDto {

    UUID id;
    String username;
    String password;
    String cdt;
    String lastActivity;
    Set<RoleDto> authRoles;
    boolean blocked;

}
