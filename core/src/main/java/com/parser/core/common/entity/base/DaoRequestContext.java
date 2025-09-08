package com.parser.core.common.entity.base;

import com.parser.core.common.jdbc.Dao;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DaoRequestContext<D extends Dao<?>> {
    Connection connection;
}
