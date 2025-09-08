package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.Date;
import java.sql.SQLException;

public interface DateBiConsumer<E> extends BiConsumerE<E, Date, SQLException> {
    static <T> DateBiConsumer<T> of(DateBiConsumer<T> consumer) {
        return consumer;
    }
}
