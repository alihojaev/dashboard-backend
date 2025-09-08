package com.parser.core.util.validation.validator;

import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.enums.FieldType;
import com.parser.core.util.validator.RequiredField;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ModelParserUtil {

    public static List<RequiredField> parse(Object model) {
        if (!(model instanceof BaseEntity)) return null;

        try {
            var requiredFields = new ArrayList<RequiredField>();
            var fields = new ArrayList<Field>();
            var clazz = model.getClass();
            for (; clazz != Object.class; ) {
                fields.addAll(List.of(clazz.getDeclaredFields()));
                clazz = clazz.getSuperclass();
            }

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = null;
                fieldValue = field.get(model);
                var fieldType = instanceOf(field.getType());
                var annotation = Optional.ofNullable(field.getAnnotation(ApiModelProperty.class));
                requiredFields.add(
                        new RequiredField(
                                field.getName(),
                                annotation
                                        .map(ApiModelProperty::value).orElse(null),
                                annotation.map(ApiModelProperty::required).orElse(false),
                                fieldValue != null,
                                fieldType,
                                annotation.map(ApiModelProperty::notes).orElse(null),
                                options(fieldType, field)
                        )
                );
                field.setAccessible(false);
            }
            return requiredFields;
        } catch (ClassNotFoundException | IllegalAccessException e) {
            log.error("ModelParserUtil parse() method", e);
        }
        return null;
    }

    private static FieldType instanceOf(Class fieldClass) {
        if (Number.class.isAssignableFrom(fieldClass)) return FieldType.NUMBER;
        else if (fieldClass.equals(Boolean.class)) return FieldType.BOOLEAN;
        else if (fieldClass.equals(String.class)) return FieldType.STRING;
        else if (fieldClass.isEnum()) return FieldType.ENUM;
        throw new IllegalStateException("Undefined data type");
    }

    private static Object options(FieldType fieldType, Field field) throws ClassNotFoundException {
        return switch (fieldType) {
            case ENUM -> Class.forName(field.getType().getName()).getEnumConstants();
            default -> null;
        };
    }
}
