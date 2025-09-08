package com.parser.core.util.validation;

import com.parser.core.common.entity.base.IdBased;
import com.parser.core.exceptions.ValidationException;

import java.util.UUID;

public interface Validatable {

    String validateMessage();

    default void validate() throws ValidationException {
        var r = validateMessage();
        if (r != null) throw new ValidationException(this, r);
    }

    static String validateId(IdBased id, String message) {
        return id == null ? message : validateId(id.getId());
    }

    static String validateId(UUID id) {
        return id == null || isValidUUID(id) ? null : "Не верный диапазон id";
    }

    private static boolean isValidUUID(UUID uuid) {
        try {
            var verify = UUID.fromString(uuid.toString());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
