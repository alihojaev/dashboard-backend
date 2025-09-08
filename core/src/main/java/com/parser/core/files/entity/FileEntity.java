package com.parser.core.files.entity;

import com.parser.core.files.enums.MinioBucket;
import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = FileEntity.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileEntity {

    @SqlTable
    public static final String TABLE_NAME = "files";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MinioBucket bucket;

    String extension;

    @Column(nullable = false, unique = true)
    String name;
    String path;

    LocalDateTime cdt;
    LocalDateTime rdt;

    public FileEntity(UUID id) {
        this.id = id;
    }
}