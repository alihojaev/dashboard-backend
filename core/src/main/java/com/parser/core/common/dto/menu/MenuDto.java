package com.parser.core.common.dto.menu;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuDto extends BaseMenuDto {
    String group;
    List<MenuSubDto> children;

    @Builder
    MenuDto(String title, String icon, String name, String component, String group, List<MenuSubDto> children) {
        super(title, icon, name, component);
        this.group = group;
        this.children = children;
    }
}
