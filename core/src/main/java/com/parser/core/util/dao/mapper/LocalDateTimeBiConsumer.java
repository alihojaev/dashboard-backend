package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;
import java.time.LocalDateTime;

public interface LocalDateTimeBiConsumer<E> extends BiConsumerE<E, LocalDateTime, SQLException> {
    static <T> LocalDateTimeBiConsumer<T> of(LocalDateTimeBiConsumer<T> consumer) {
        return consumer;
    }
}
