package com.parser.core.common.jdbc;

import com.parser.core.common.entity.base.DaoRequestContext;
import com.parser.core.common.entity.base.IdBased;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.TransactionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class BaseEntityDao<E extends IdBased, C extends DaoRequestContext<? extends Dao<E>>> extends BaseDao<E, C> implements EntityDao<E> {

    @Getter
    String tableName;

    protected BaseEntityDao(DataSource dataSource, String tableName) {
        super(dataSource);
        this.tableName = tableName;
    }

    @Override
    public Optional<E> getById(UUID id) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> getById(id, connection)
        );
    }

    @Override
    public Optional<E> getById(UUID id, Connection connection) throws SQLException {
        return SqlBuilder
                .querySelect(rs -> Optional.ofNullable(rs.next() ? map(rs, createContext(connection)) : null))
                .table(getTableName())
                .where("ID = ?")
                .select()
                .statement(statement -> statement.setObject(1, id))
                .exec(connection);
    }

    @Override
    public E save(E e, Connection connection) throws SQLException {
        if (e.getId() == null) {
            UUID id = insert(e, connection);
            e.setId(id);
        } else {
            update(e, connection);
        }

        return e;
    }


    @Override
    public void delete(E e, Connection connection) throws SQLException {
        SqlBuilder.queryDelete(getTableName())
                .where("ID = ?")
                .delete()
                .statement(statement -> statement.setObject(1, e.getId()))
                .execRequireAffected(connection);
    }
}
