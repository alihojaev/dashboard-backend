package com.parser.core.auth.screen.jdbc;


import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.auth.screen.entity.Screen;
import com.parser.core.common.entity.base.DaoRequestContext;
import com.parser.core.common.jdbc.BaseEntityDao;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.TransactionUtil;
import com.parser.core.util.dao.mapper.RSMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class ScreenDaoImpl extends BaseEntityDao<Screen, DaoRequestContext<ScreenDao>> implements ScreenDao {

    ScreenDaoImpl(DataSource dataSource) {
        super(dataSource, Screen.TABLE_NAME);
    }

    @Override
    protected DaoRequestContext<ScreenDao> createContext(Connection connection) {
        return new DaoRequestContext<>(connection);
    }

    @Override
    public Screen map(ResultSet rs, DaoRequestContext<ScreenDao> context) throws SQLException {
        return RSMapper.map(rs, Screen::new, rsMapper -> rsMapper
                .mapIdBase()
                .map("NAME", Screen::setName, ScreenType.class)
                .map("DESCRIPTION", Screen::setDescription)
                .map("CDT", Screen::setCdt)
        );
    }

    @Override
    public UUID insert(Screen screen, Connection connection) throws SQLException {
        return SqlBuilder.queryInsert(Screen.TABLE_NAME)
                .primaryKey(Screen.SEQ_NAME)
                .column("NAME", screen.getName())
                .column("DESCRIPTION", screen.getDescription())
                .column("CDT", screen.getCdt())
                .insert()
                .exec(connection);
    }

    @Override
    public void update(Screen screen, Connection connection) throws SQLException {
        SqlBuilder.queryUpdate(Screen.TABLE_NAME)
                .column("NAME", screen.getName())
                .column("DESCRIPTION", screen.getDescription())
                .column("CDT", screen.getCdt())
                .where("ID = ?")
                .update()
                .value(screen)
                .exec(connection);
    }

    @Override
    public List<Screen> listAll() {
        return TransactionUtil.transaction(
                dataSource,
                connection -> SqlBuilder.querySelect(rs -> {
                            List<Screen> result = new ArrayList<>();
                            while (rs.next()) {
                                try {
                                    result.add(map(rs, createContext(connection)));
                                } catch (IllegalArgumentException e) {
                                    log.error("listAll()", e);
                                }
                            }
                            return result;
                        })
                        .table(Screen.TABLE_NAME)
                        .exec(connection)
        );
    }

    @Override
    public Optional<Screen> findByName(ScreenType name) {
        return TransactionUtil.transaction(
                dataSource,
                connection -> SqlBuilder.querySelect(rs -> Optional.ofNullable(rs.next() ? map(rs, createContext(connection)) : null))
                        .table(Screen.TABLE_NAME)
                        .where("name = ?")
                        .value(name)
                        .exec(connection)
        );
    }
}
