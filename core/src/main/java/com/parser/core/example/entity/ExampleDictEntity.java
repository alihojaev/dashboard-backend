package com.parser.core.example.entity;

import com.parser.core.common.dict.BaseDict;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@NoArgsConstructor
@Table(name = ExampleDictEntity.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExampleDictEntity extends BaseDict {

    @SqlTable
    public static final String TABLE_NAME = "example_dict";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    private UUID id;

    String text;
    Long number;
    BigDecimal amount;
    LocalDateTime dateTime;
    LocalDate date;
    LocalTime time;

    public ExampleDictEntity(UUID id) {
        this.id = id;
    }

    public ExampleDictEntity(UUID id, String name, String description) {
        super(name, description);
        this.id = id;
    }
}