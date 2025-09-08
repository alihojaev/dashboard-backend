package com.parser.core.files.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum MinioBucket {

    photos("Фото"),
    ;

    String description;

    public static MinioBucket fromName(String name) {
        return name == null ? null : valueOf(name);
    }
}
