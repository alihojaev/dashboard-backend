package com.parser.core.auth.screen.dto;

import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.common.entity.base.IdBased;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScreenDto implements IdBased {

    UUID id;
    ScreenType name;
    String description;
    Timestamp cdt;

}
