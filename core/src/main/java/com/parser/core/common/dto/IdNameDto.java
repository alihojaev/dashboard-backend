package com.parser.core.common.dto;

import com.parser.core.util.validation.ChainValidator;
import com.parser.core.util.validation.Validatable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdNameDto implements Validatable {

    UUID id;
    String name;

    @Override
    public String validateMessage() {
        return ChainValidator.create()
                .thenNotNull(this::getName, "name not specified")
                .validateMessage();
    }

}
