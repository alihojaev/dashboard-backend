package com.parser.core.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FieldType {
    BOOLEAN,
    NUMBER,
    STRING,
    BASE_DICT_MODEL,
    ENUM;

    public static FieldType fromName(String name) {
        return name == null ? null : valueOf(name);
    }
}
