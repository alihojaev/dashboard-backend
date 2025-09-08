package com.parser.core.util.validation;

import com.google.common.base.Strings;
import com.parser.core.common.entity.base.IdBased;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public interface ChainValidator {

    ChainElement then(Validatable validatable);

    default ChainElement then(UUID id) {
        return then(() -> id != null && isValidUUID(id) ? "Не верный диапазон id" : null);
    }

    default ChainElement then(Supplier<IdBased> supplier, String message) {
        return then(() -> {
            var idBased = supplier.get();
            if (idBased == null) return Objects.requireNonNull(message);
            var id = idBased.getId();
            return id != null && isValidUUID(id) ? "Не верный диапазон id" : null;
        });
    }

    default ChainElement thenNotEmpty(Supplier<String> supplier, String message) {
        return then(() -> Strings.isNullOrEmpty(supplier.get()) ? message : null);
    }

    default ChainElement thenNotNull(Supplier<?> supplier, String message) {
        return then(() -> supplier.get() == null ? message : null);
    }

    default ChainElement thenMatch(Supplier<String> supplier, String regex, String message) {
        return then(() -> supplier.get().matches(regex) ? null : message);
    }

    default ChainElement thenMatchOrNull(Supplier<String> supplier, String regex, String message) {
        return then(() -> Strings.isNullOrEmpty(supplier.get()) ? null : supplier.get().matches(regex) ? null : message);
    }

    default ChainElement thenIsPositiveBigDecimalValue(BigDecimal value, String message) {
        return then(() -> value == null || value.signum() < 0 ? message : null);
    }

    default ChainElement thenIsPositiveBigDecimalValueOrNull(BigDecimal value, String message) {
        return then(() -> value == null ? null : (value.signum() < 0 ? message : null));
    }

    default <U extends Comparable<U>> ChainElement thenInRange(U value, U min, U max) {
        if (min.compareTo(max) > 0) throw new IllegalStateException("минимум не может быть больше чем максимум");
        return then(() ->
                (value.compareTo(min) >= 0) &&
                        (value.compareTo(max) <= 0) ?
                        null :
                        "Значение должно быть в диапазоне от " + min + " до " + max
        );
    }

    default <U extends Comparable<U>> ChainElement thenInRangeOrNull(U value, U min, U max) {
        if (min.compareTo(max) > 0) throw new IllegalStateException("минимум не может быть больше чем максимум");
        return then(() ->
                value == null ? null : (value.compareTo(min) >= 0) &&
                        (value.compareTo(max) <= 0) ?
                        null :
                        "Значение должно быть в диапазоне от " + min + " до " + max
        );
    }

    default ChainElement thenIsPositive(Long value, String message) {
        return then(() -> value == null || value < 0 ? message : null);
    }

    default ChainElement thenIsPositive(BigDecimal value, String message) {
        return then(() -> value == null || value.compareTo(BigDecimal.ZERO) < 0 ? message : null);
    }

    default ChainElement thenIsPositiveOrNull(Long value, String message) {
        return then(() -> value != null ? (value < 0 ? message : null) : null);
    }

    default <U extends Comparable<U>> ChainElement thenLess(U first, U second, String message) {
        return then(() -> first.compareTo(second) < 0 ? null : message);
    }

    default <U extends Comparable<U>> ChainElement thenLessOrEquals(U first, U second, String message) {
        return then(() -> first.compareTo(second) <= 0 ? null : message);
    }

    default <U extends Comparable<U>> ChainElement thenMore(U first, U second, String message) {
        return then(() -> first.compareTo(second) > 0 ? null : message);
    }

    default <U extends Comparable<U>> ChainElement thenMoreOrEquals(U first, U second, String message) {
        return then(() -> first.compareTo(second) >= 0 ? null : message);
    }

    default ChainElement thenPassword(Supplier<String> supplier) {
        return then(() -> {
            var p = supplier.get();
            if (p == null || p.isEmpty() || p.isBlank()) return "Пароль не может быть пустым";
            if (p.length() < 8 || p.length() > 20) return "Пароль должен быть от 8-ми до 20-ти символов";
            if (!p.matches("^[0-9a-zA-Z!@#$%^&*(){}\\[\\]_\\-=+~`/\\\\,.<>]{8,20}$"))
                return "Пароль может состоять из цифр, символов латинских нижнего и верхнего регистра и спецсимволов \"!@#$%^&*(){}[]_-=+~`/\\,.<>\"";
            if (!p.matches(".*[0-9]+.*")) return "Пароль должен содержать хотя бы одну цифру";
            if (!p.matches(".*[a-z]+.*"))
                return "Пароль должен содержать хотя бы одну латинскую букву в нижнем регистре";
            if (!p.matches(".*[A-Z]+.*"))
                return "Пароль должен содержать хотя бы одну латинскую букву в верхнем регистре";
            if (!p.matches(".*[!@#$%^&*(){}\\[\\]_\\-=+~`/\\\\,.<>]+.*"))
                return "Пароль должен содержать хотя бы один спецсимвол";
            return null;
        });
    }


    static ChainHead create() {
        return new ChainHead();
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
