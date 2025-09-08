package com.parser.core.util.dao.query;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.function.BiConsumer;

@AllArgsConstructor
@FieldDefaults(makeFinal = true)
class ParentHolder<T> {
    T parent;
    BiConsumer<T, JoinConfig> addConfigToParent;

    void addConfig(JoinConfig joinConfig) {
        addConfigToParent.accept(parent, joinConfig);
    }
}
