package com.parser.core.auth.login.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LoginStatusEnum {
    SUCCESS("Success"),
    WRONG_PASSWORD("Wrong password"),
    ERROR("Error");

    String description;

    public static LoginStatusEnum fromName(String name) {
        return name == null ? null : valueOf(name);
    }
}
