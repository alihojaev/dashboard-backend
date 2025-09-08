package com.parser.server.controllers;

import com.parser.core.auth.core.dto.GrantHolder;
import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.common.dto.menu.PermissionGroup;
import com.parser.core.config.permission.annotation.ManualPermissionControl;
import com.parser.server.config.backend.BackendTokenContextHolder;
import com.parser.server.config.grant.GrantService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuController {

    GrantService grant;

    @GetMapping("/api/menu")
    @ManualPermissionControl
    public List<PermissionGroup> menu() {
        var manager = BackendTokenContextHolder.current().getPrincipal();
        return manager.getAuthorities().stream()
                .filter(gh -> gh.hasAny(AccessType.READ) && gh.getPermission().getView() != null)
                .map(GrantHolder::getPermission)
                .collect(Collectors.groupingBy(PermissionType::getScreenType))
                .entrySet().stream()
                .map(entry -> new PermissionGroup(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparingInt(Enum::ordinal))
                                .collect(Collectors.toList())
                ))
                .sorted(Comparator.comparingInt(o -> o.getScreen().ordinal()))
                .collect(Collectors.toList());
    }
}