package com.parser.core.auth.role.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum ScreenType {
    GENERAL("Основные", false, "solar:home-2-linear"),
    SETTINGS("Настройки", false,"solar:crown-line-linear"),
    DICT("Справочники", false, "solar:document-text-linear"),
    ;

    String description;
    Boolean expanded;
    String icon;

    public static ScreenType fromName(String name) {
        return name == null ? null : valueOf(name);
    }
}
