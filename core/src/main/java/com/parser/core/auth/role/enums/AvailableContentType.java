package com.parser.core.auth.role.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum AvailableContentType {

    AUTH("Auth"),
    AVAILABLE_CONTENT("Available content"),
    ;

    String description;

    public static AvailableContentType fromName(String name) {
        return name == null ? null : valueOf(name);
    }
}
