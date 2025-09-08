package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;

public interface EnumBiConsumer<E, T extends Enum<T>> extends BiConsumerE<E, T, SQLException> {
    static <E, T extends Enum<T>> EnumBiConsumer<E, T> of(EnumBiConsumer<E, T> consumer) {
        return consumer;
    }
}
