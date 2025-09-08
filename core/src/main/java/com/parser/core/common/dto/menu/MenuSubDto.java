package com.parser.core.common.dto.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuSubDto extends BaseMenuDto {
    String badge;

    @Builder
    MenuSubDto(String title, String icon, String name, String component, String badge) {
        super(title, icon, name, component);
        this.badge = badge;
    }
}
