package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;
import java.time.ZonedDateTime;

public interface ZonedDateTimeBiConsumer<E> extends BiConsumerE<E, ZonedDateTime, SQLException> {
    static <T> ZonedDateTimeBiConsumer<T> of(ZonedDateTimeBiConsumer<T> consumer) {
        return consumer;
    }
}
