package com.parser.core.util.dao.mapper;

import com.parser.core.util.functional.BiConsumerE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

public class RSMapperInner<E, P, R extends RSMapper<P, R>> extends RSMapper<E, RSMapperInner<E, P, R>> {

    private final R parent;

    protected RSMapperInner(ResultSet resultSet, String prefix, E e, R parent) {
        super(resultSet, prefix, e);
        this.parent = parent;
    }

    public R ret(BiConsumerE<P, E, SQLException> combiner) throws SQLException {
        combiner.accept(parent.e, this.e);
        return parent;
    }

    public <T> RSMapperInner<T, E, RSMapperInner<E, P, R>> deeper(Supplier<T> supplier) {
        return new RSMapperInner<>(resultSet, prefix, supplier.get(), this);
    }
}
