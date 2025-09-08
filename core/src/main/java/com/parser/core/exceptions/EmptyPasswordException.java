package com.parser.core.exceptions;

public class EmptyPasswordException extends BadRequestException {

    public static final String MESSAGE = "Пароль должен быть задан";

    public EmptyPasswordException() {
        super(MESSAGE);
    }
}
