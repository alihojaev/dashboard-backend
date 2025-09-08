package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;

public interface IntegerBiConsumer<E> extends BiConsumerE<E, Integer, SQLException> {
    static <T> IntegerBiConsumer<T> of(IntegerBiConsumer<T> consumer) {
        return consumer;
    }
}
