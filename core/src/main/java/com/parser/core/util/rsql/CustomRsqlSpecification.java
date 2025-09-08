package com.parser.core.util.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomRsqlSpecification<T> implements Specification<T> {

    private final String property;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    public CustomRsqlSpecification(String property, ComparisonOperator operator, List<String> arguments) {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = getPath(root, property);

        switch (operator.getSymbol()) {
            case "=notnull=":
                return builder.isNotNull(path);
            case "=isnull=":
                return builder.isNull(path);
            case "==":
                Object castedValue = castArgument(path, arguments.get(0));
                // Если путь указывает на сущность, а значение - строка, ищем по полю color
                if (!path.getJavaType().equals(String.class) && castedValue instanceof String) {
                    // Проверяем, есть ли поле 'color' в текущей сущности
                    try {
                        Path<?> colorPath = path.get("color");
                        if (colorPath != null && colorPath.getJavaType().equals(String.class)) {
                            return builder.equal(colorPath, castedValue);
                        }
                    } catch (Exception e) {
                        // Если поле 'color' не найдено, используем обычное сравнение
                    }
                }
                return builder.equal(path, castedValue);
            case "!=":
                return builder.notEqual(path, castArgument(path, arguments.get(0)));
            case ">":
            case "=gt=":
                return greaterThanPredicate(builder, path, arguments.get(0));
            case ">=":
            case "=ge=":
                return greaterThanOrEqualPredicate(builder, path, arguments.get(0));
            case "<":
            case "=lt=":
                return lessThanPredicate(builder, path, arguments.get(0));
            case "<=":
            case "=le=":
                return lessThanOrEqualPredicate(builder, path, arguments.get(0));
            case "=in=":
                return path.in(castArguments(path, arguments));
            case "=out=":
                return builder.not(path.in(castArguments(path, arguments)));
            case "=member=":
                Path<?> pathForMember = getPath(root, property);

                @SuppressWarnings("unchecked")
                Expression<Collection<Object>> collectionPath = (Expression<Collection<Object>>) pathForMember;

                return builder.isMember(
                        castArgument(pathForMember, arguments.get(0)),
                        collectionPath
                );
            case "=like=":
                String raw = arguments.get(0).toLowerCase();
                String pattern = raw.replace("*", "%");
                // Если путь указывает на сущность, а не на строку, ищем по полю color
                if (!path.getJavaType().equals(String.class)) {
                    try {
                        Path<?> colorPath = path.get("color");
                        if (colorPath != null && colorPath.getJavaType().equals(String.class)) {
                            return builder.like(
                                    builder.lower(colorPath.as(String.class)),
                                    pattern
                            );
                        }
                    } catch (Exception e) {
                        // Если поле 'color' не найдено, используем обычное сравнение
                    }
                }
                return builder.like(
                        builder.lower(path.as(String.class)),
                        pattern
                );
            case "=ilike=":
                return builder.like(builder.lower(path.as(String.class)), castArgument(path, arguments.get(0)).toString().toLowerCase().replace("*", "%"));
            default:
                throw new UnsupportedOperationException("Оператор " + operator + " не поддерживается.");
        }
    }

    private Path<?> getPath(From<?, ?> root, String property) {
        String[] parts = property.split("\\.");
        Path<?> path = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            // Проверяем, является ли текущий путь коллекцией
            if (Collection.class.isAssignableFrom(path.getJavaType())) {
                // Если это коллекция, делаем join
                path = ((From<?, ?>) path).join(part, JoinType.LEFT);
            } else {
                // Проверяем, является ли следующее поле коллекцией
                try {
                    Class<?> fieldType = path.get(part).getJavaType();
                    if (Collection.class.isAssignableFrom(fieldType)) {
                        // Если следующее поле - коллекция, делаем join
                        path = ((From<?, ?>) path).join(part, JoinType.LEFT);
                    } else {
                        // Обычное поле
                        path = path.get(part);
                    }
                } catch (Exception e) {
                    // Если не можем получить тип, пробуем обычный get
                    path = path.get(part);
                }
            }
        }

        return path;
    }

    private Predicate greaterThanPredicate(CriteriaBuilder builder, Path<?> path, String argument) {
        Class<?> type = path.getJavaType();

        if (type.equals(LocalDateTime.class)) {
            return builder.greaterThan(path.as(LocalDateTime.class), (LocalDateTime) castArgument(path, argument));
        } else if (type.equals(LocalDate.class)) {
            return builder.greaterThan(path.as(LocalDate.class), (LocalDate) castArgument(path, argument));
        } else if (type.equals(BigDecimal.class)) {
            return builder.greaterThan(path.as(BigDecimal.class), (BigDecimal) castArgument(path, argument));
        } else if (type.equals(Integer.class)) {
            return builder.greaterThan(path.as(Integer.class), (Integer) castArgument(path, argument));
        } else if (type.equals(Long.class)) {
            return builder.greaterThan(path.as(Long.class), (Long) castArgument(path, argument));
        } else {
            throw new UnsupportedOperationException("Тип " + type.getSimpleName() + " не поддерживается для операции >");
        }
    }

    private Predicate greaterThanOrEqualPredicate(CriteriaBuilder builder, Path<?> path, String argument) {
        Class<?> type = path.getJavaType();
        if (type.equals(LocalDateTime.class)) {
            return builder.greaterThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) castArgument(path, argument));
        } else if (type.equals(LocalDate.class)) {
            return builder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) castArgument(path, argument));
        } else if (type.equals(BigDecimal.class)) {
            return builder.greaterThanOrEqualTo(path.as(BigDecimal.class), (BigDecimal) castArgument(path, argument));
        } else if (type.equals(Integer.class)) {
            return builder.greaterThanOrEqualTo(path.as(Integer.class), (Integer) castArgument(path, argument));
        } else if (type.equals(Long.class)) {
            return builder.greaterThanOrEqualTo(path.as(Long.class), (Long) castArgument(path, argument));
        }
        throw new UnsupportedOperationException("Тип " + type.getSimpleName() + " не поддерживается для >=.");
    }

    private Predicate lessThanPredicate(CriteriaBuilder builder, Path<?> path, String argument) {
        Class<?> type = path.getJavaType();
        if (type.equals(LocalDateTime.class)) {
            return builder.lessThan(path.as(LocalDateTime.class), (LocalDateTime) castArgument(path, argument));
        } else if (type.equals(LocalDate.class)) {
            return builder.lessThan(path.as(LocalDate.class), (LocalDate) castArgument(path, argument));
        } else if (type.equals(BigDecimal.class)) {
            return builder.lessThan(path.as(BigDecimal.class), (BigDecimal) castArgument(path, argument));
        } else if (type.equals(Integer.class)) {
            return builder.lessThan(path.as(Integer.class), (Integer) castArgument(path, argument));
        } else if (type.equals(Long.class)) {
            return builder.lessThan(path.as(Long.class), (Long) castArgument(path, argument));
        }
        throw new UnsupportedOperationException("Тип " + type.getSimpleName() + " не поддерживается для <.");
    }

    private Predicate lessThanOrEqualPredicate(CriteriaBuilder builder, Path<?> path, String argument) {
        Class<?> type = path.getJavaType();
        if (type.equals(LocalDateTime.class)) {
            return builder.lessThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) castArgument(path, argument));
        } else if (type.equals(LocalDate.class)) {
            return builder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) castArgument(path, argument));
        } else if (type.equals(BigDecimal.class)) {
            return builder.lessThanOrEqualTo(path.as(BigDecimal.class), (BigDecimal) castArgument(path, argument));
        } else if (type.equals(Integer.class)) {
            return builder.lessThanOrEqualTo(path.as(Integer.class), (Integer) castArgument(path, argument));
        } else if (type.equals(Long.class)) {
            return builder.lessThanOrEqualTo(path.as(Long.class), (Long) castArgument(path, argument));
        }
        throw new UnsupportedOperationException("Тип " + type.getSimpleName() + " не поддерживается для <=.");
    }

    private Object castArgument(Path<?> path, String argument) {
        argument = argument.replace("%20", " ");

        Class<?> type = path.getJavaType();
        try {
            if (type.equals(Integer.class)) {
                return Integer.valueOf(argument);
            } else if (type.equals(Long.class)) {
                return Long.valueOf(argument);
            } else if (type.equals(Double.class)) {
                return Double.valueOf(argument);
            } else if (type.equals(LocalDateTime.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(argument, formatter);
            } else if (type.equals(LocalDate.class)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(argument, formatter);
            } else if (type.equals(UUID.class)) {
                return UUID.fromString(argument);
            } else if (type.equals(BigDecimal.class)) {
                return new BigDecimal(argument);
            } else if (type.equals(String.class)) {
                return argument;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Невозможно преобразовать аргумент: " + argument + " в тип " + type.getSimpleName(), e);
        }
        return argument;
    }

    private List<Object> castArguments(Path<?> path, List<String> arguments) {
        return arguments.stream().map(arg -> castArgument(path, arg)).collect(Collectors.toList());
    }
}