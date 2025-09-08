package com.parser.core.example.dto;

import com.parser.core.util.validation.ChainValidator;
import com.parser.core.util.validation.Validatable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExampleDictDto implements Validatable {

    UUID id;
    String text;
    String name;
    String description;
    Long number;
    BigDecimal amount;
    LocalDate date;
    LocalDateTime dateTime;
    LocalTime time;
    LocalDateTime cdt;

    @Override
    public String validateMessage() {
        return ChainValidator.create()
                .thenNotNull(this::getName, "name not set")
                .validateMessage();
    }
}
