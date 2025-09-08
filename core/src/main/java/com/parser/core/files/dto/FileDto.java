package com.parser.core.files.dto;

import com.parser.core.files.enums.MinioBucket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDto {

    UUID id;
    MinioBucket bucket;
    String extension;
    String name;
    String path;

}
