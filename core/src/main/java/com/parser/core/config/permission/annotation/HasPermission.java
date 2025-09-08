package com.parser.core.config.permission.annotation;

import com.parser.core.auth.role.enums.AccessType;
import com.parser.core.auth.role.enums.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface HasPermission {

    PermissionType value();

    AccessType[] access() default {AccessType.ALL};
}
