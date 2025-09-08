package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;

public interface DoubleBiConsumer<E> extends BiConsumerE<E, Double, SQLException> {
    static <T> DoubleBiConsumer<T> of(DoubleBiConsumer<T> consumer) {
        return consumer;
    }
}
