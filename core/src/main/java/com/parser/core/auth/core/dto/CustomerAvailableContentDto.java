package com.parser.core.auth.core.dto;

import com.parser.core.auth.role.enums.AvailableContentType;
import com.parser.core.common.entity.base.IdBased;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAvailableContentDto implements IdBased {
    UUID id;
    AvailableContentType availableContentType;
    Boolean enabled;
    Timestamp cdt;
    Timestamp lastSwitched;
}
