package com.parser.core.auth.core.entity;

import com.parser.core.auth.role.enums.AvailableContentType;
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
@Table(name = CustomerAvailableContent.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAvailableContent implements IdBased {

    @SqlTable
    public static final String TABLE_NAME = "client_available_content";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AvailableContentType availableContentType;

    @Column(nullable = false, length = 1024)
    String description;

    @Column(nullable = false)
    Boolean enabled;

    @Column(nullable = false)
    Timestamp cdt;

    @Column
    Timestamp lastSwitched;
}
