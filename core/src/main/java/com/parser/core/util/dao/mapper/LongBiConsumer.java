package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;
import com.parser.core.util.functional.FunctionE;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface LongBiConsumer<E> extends BiConsumerE<E, Long, SQLException> {

    static <E> LongBiConsumer<E> of(LongBiConsumer<E> consumer) {
        return consumer;
    }

    static <E, T> LongBiConsumer<E> ofIntermediate(FunctionE<Long, T, SQLException> intermediateMapper, BiConsumer<E, T> consumer) {
        return of((e, aLong) -> consumer.accept(e, intermediateMapper.apply(aLong)));
    }

    static <E, T> LongBiConsumer<E> ofIntermediateOptional(FunctionE<Long, Optional<T>, SQLException> intermediateMapper, BiConsumer<E, T> consumer) {
        return of((e, aLong) -> consumer.accept(e, intermediateMapper.apply(aLong).orElseThrow()));
    }
}
