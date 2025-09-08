package com.parser.core.util.dao.mapper;


import com.parser.core.util.functional.BiConsumerE;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface BigDecimalBiConsumer<E> extends BiConsumerE<E, BigDecimal, SQLException> {
    static <T> BigDecimalBiConsumer<T> of(BigDecimalBiConsumer<T> consumer) {
        return consumer;
    }
}
