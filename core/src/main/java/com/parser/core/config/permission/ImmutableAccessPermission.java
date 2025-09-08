package com.parser.core.config.permission;

import lombok.Value;

@Value
public class ImmutableAccessPermission implements AccessPermission {
    int access;
}
