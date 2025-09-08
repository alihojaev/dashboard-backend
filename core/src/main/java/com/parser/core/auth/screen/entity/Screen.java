package com.parser.core.auth.screen.entity;

import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Screen.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Screen implements IdBased {

    @SqlTable
    public static final String TABLE_NAME = "SCREEN";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    ScreenType name;

    @Column(length = 512)
    String description;

    @Column(nullable = false)
    Timestamp cdt;

    public Screen(UUID id) {
        this.id = id;
    }
}
