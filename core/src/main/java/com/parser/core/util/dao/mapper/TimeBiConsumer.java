package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;
import java.sql.Time;

public interface TimeBiConsumer<E> extends BiConsumerE<E, Time, SQLException> {
    static <T> TimeBiConsumer<T> of(TimeBiConsumer<T> consumer) {
        return consumer;
    }
}
