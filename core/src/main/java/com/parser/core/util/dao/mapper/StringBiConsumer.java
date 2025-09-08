package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.FunctionE;

import java.sql.SQLException;
import java.util.function.BiConsumer;

public interface StringBiConsumer<E> extends BiConsumerE<E, String, SQLException> {
    static <T> StringBiConsumer<T> of(StringBiConsumer<T> consumer) {
        return consumer;
    }

    static <E, T> StringBiConsumer<E> ofIntermediate(FunctionE<String, T, SQLException> intermediateMapper, BiConsumer<E, T> consumer) {
        return of((e, str) -> consumer.accept(e, intermediateMapper.apply(str)));
    }
}
