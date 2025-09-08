package com.parser.core.exceptions;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class InvalidUserStatusException extends BaseException {

    public InvalidUserStatusException(@NotNull String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
