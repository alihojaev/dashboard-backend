package com.parser.core.auth.role.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Stream;


@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum PermissionType implements GrantedAuthority {

    DASHBOARD("Главная", ScreenType.GENERAL, "/", "solar:home-2-linear"),

    EXAMPLE("Example", ScreenType.DICT, "/example", "solar:user-linear"),

    ADMINS("Админы", ScreenType.SETTINGS, "/users", "solar:user-linear"),
    ROLE("Роли", ScreenType.SETTINGS, "/role", "solar:shield-keyhole-outline"),

    ;

    String description;
    ScreenType screenType;
    String view;
    String icon;

    public static PermissionType fromName(String name) {
        return name == null ? null : valueOf(name);
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public static ScreenType screenFromPermission(PermissionType perm) {
        return mappingCache.entrySet().stream()
                .filter(entry -> entry.getValue().contains(perm))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
    }

    private static final EnumMap<ScreenType, EnumSet<PermissionType>> mappingCache;

    static {
        mappingCache = new EnumMap<>(ScreenType.class);

        Stream.of(values())
                .forEach(perm -> mappingCache.compute(
                        perm.getScreenType(),
                        (screenType, permissionTypes) -> {
                            if (permissionTypes == null) {
                                return EnumSet.of(perm);
                            } else {
                                permissionTypes.add(perm);
                                return permissionTypes;
                            }
                        }
                ));
    }
}
