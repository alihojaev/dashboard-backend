package com.parser.core.common.dto.menu;

import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.auth.role.enums.ScreenType;
import lombok.Value;

import java.util.List;

@Value
public class PermissionGroup {
    ScreenType screen;
    List<PermissionType> permissions;
}
