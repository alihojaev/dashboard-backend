package com.parser.core.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ValidationException extends BadRequestException {
    private Object requestModel;

    public ValidationException(@NotNull String message) {
        super(message);
    }

    public ValidationException(Object requestModel, String message) {
        super(message);
        this.requestModel = requestModel;
    }

    public static Supplier<ValidationException> fromMessage(String message) {
        return () -> new ValidationException(null, message);
    }

    public Object getRequestModel() {
        return requestModel;
    }
}
