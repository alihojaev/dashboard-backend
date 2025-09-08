package com.parser.core.common.dto;

import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.validation.ChainElement;
import com.parser.core.util.validation.ChainValidator;
import com.parser.core.util.validation.Validatable;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseDictDto implements Validatable, IdBased {

    @ApiModelProperty(value = "id", dataType = "UUID")
    UUID id;
    @ApiModelProperty(value = "название", dataType = "String")
    String name;
    @ApiModelProperty(value = "описание", dataType = "String")
    String description;

    @Override
    public String validateMessage() {
        return baseValidator()
                .validateMessage();
    }

    protected ChainElement baseValidator() {
        return ChainValidator.create()
                .then(getId())
                .thenNotEmpty(this::getName, "Не указано название");
    }
}
