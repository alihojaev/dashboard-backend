package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface TimestampBiConsumer<E> extends BiConsumerE<E, Timestamp, SQLException> {
    static <T> TimestampBiConsumer<T> of(TimestampBiConsumer<T> consumer) {
        return consumer;
    }
}
