package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.SQLException;
import java.util.UUID;

public interface UUIDBiConsumer<E> extends BiConsumerE<E, UUID, SQLException> {
    static <E> UUIDBiConsumer<E> of(UUIDBiConsumer<E> consumer) {
        return consumer;
    }
}
