package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;

public interface BooleanBiConsumer<E> extends BiConsumerE<E, Boolean, SQLException> {
    static <T> BooleanBiConsumer<T> of(BooleanBiConsumer<T> consumer) {
        return consumer;
    }
}
