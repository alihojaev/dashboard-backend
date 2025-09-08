package com.parser.core.exceptions;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;


public class BadRequestException extends BaseException {

    public BadRequestException(@NotNull String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}

