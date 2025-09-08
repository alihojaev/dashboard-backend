package com.parser.core.util.dao.mapper;

import java.sql.ResultSet;
import java.util.function.Supplier;

public class RSMapperUpper<E> extends RSMapper<E, RSMapperUpper<E>> {

    protected RSMapperUpper(ResultSet resultSet, String prefix, E e) {
        super(resultSet, prefix, e);
    }

    public <T> RSMapperInner<T, E, RSMapperUpper<E>> deeper(Supplier<T> supplier) {
        return new RSMapperInner<>(resultSet, prefix, supplier.get(), this);
    }
}
