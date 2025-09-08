package com.parser.core.exceptions;

public class UserNotFoundException extends NotFoundException {
    public static final String MESSAGE = "Пользователь с указанным именем или паролем не найден.";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
