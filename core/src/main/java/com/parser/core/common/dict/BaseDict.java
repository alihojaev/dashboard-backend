package com.parser.core.common.dict;

import com.parser.core.common.entity.base.BaseEntity;
import com.parser.core.common.entity.base.IdBased;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseDict extends BaseEntity implements IdBased {

    String name;

    String description;
}
