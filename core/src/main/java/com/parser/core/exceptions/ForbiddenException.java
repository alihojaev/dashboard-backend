package com.parser.core.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public static final String DEFAULT_MESSAGE = "Access is denied!";

    public ForbiddenException() {
        super(DEFAULT_MESSAGE, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
