package com.parser.core.util.validator;

import com.parser.core.common.enums.FieldType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequiredField {
    String name;
    String description;
    Boolean required;
    Boolean filled;
    FieldType type;
    String regex;
    Object option;
}
