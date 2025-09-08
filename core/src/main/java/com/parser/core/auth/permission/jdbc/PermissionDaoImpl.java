package com.parser.core.auth.permission.jdbc;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.role.entity.Role;
import com.parser.core.auth.role.entity.RolePermission;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.common.entity.base.DaoRequestContext;
import com.parser.core.common.jdbc.BaseEntityDao;
import com.parser.core.util.dao.SqlBuilder;
import com.parser.core.util.dao.mapper.RSMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class PermissionDaoImpl extends BaseEntityDao<Permission, DaoRequestContext<PermissionDao>> implements PermissionDao {

    protected PermissionDaoImpl(DataSource dataSource) {
        super(dataSource, Permission.TABLE_NAME);
    }

    @Override
    protected DaoRequestContext<PermissionDao> createContext(Connection connection) {
        return new DaoRequestContext<>(connection);
    }

    @Override
    public Permission map(ResultSet rs, DaoRequestContext<PermissionDao> context) throws SQLException {
        return RSMapper.map(rs, Permission::new, rsMapper -> rsMapper
                .map("ID", Permission::setId)
                .map("NAME", Permission::setName, PermissionType.class)
                .map("DESCRIPTION", Permission::setDescription)
                .map("CDT", Permission::setCdt)
        );
    }

    @Override
    public UUID insert(Permission permission, Connection connection) throws SQLException {
        return SqlBuilder.queryInsert(getTableName())
                .primaryKey(Permission.SEQ_NAME)
                .column("NAME", permission.getName())
                .column("DESCRIPTION", permission.getDescription())
                .column("SCREEN_ID", permission.getScreen().getId())
                .column("CDT", permission.getCdt())
                .insert()
                .exec(connection);
    }

    @Override
    public void update(Permission permission, Connection connection) throws SQLException {
        SqlBuilder.queryUpdate(getTableName())
                .column("NAME", permission.getName())
                .column("DESCRIPTION", permission.getDescription())
                .column("SCREEN_ID", permission.getScreen().getId())
                .column("CDT", permission.getCdt())
                .where("ID = ?")
                .value(permission.getId())
                .update()
                .execRequireAffected(connection);
    }

    @Override
    public List<PermissionDto> listAll() {
        return SqlBuilder.querySelect(rs -> {
                    List<PermissionDto> list = new ArrayList<>(rs.getFetchSize());
                    while (rs.next()) {
                        try {
                            list.add(new PermissionDto(
                                    UUID.fromString(rs.getString("ID")),
                                    PermissionType.fromName(rs.getString("NAME")),
                                    15
                            ));
                        } catch (IllegalArgumentException e) {
                            log.warn("listAll(): {}", e.getMessage());
                        }
                    }
                    return list;
                })
                .table(Permission.TABLE_NAME)
                .completeColumns("ID,NAME")
                .execQuietly(dataSource);
    }

    @Override
    public List<RolePermission> listAllByRoleId(UUID roleId) {
        return SqlBuilder.select(
                        "SELECT " +
                                "p.ID," +
                                "p.NAME," +
                                "p.DESCRIPTION," +
                                "p.CDT," +
                                "rp.PERMISSION_ACCESS " +
                                "FROM " + RolePermission.TABLE_NAME + " rp " +
                                "INNER JOIN " + Permission.TABLE_NAME + " p ON p.ID = rp.PERMISSION_ID " +
                                "WHERE RP.ROLE_ID = ?",
                        rs -> {
                            List<RolePermission> result = new ArrayList<>();

                            while (rs.next()) {
                                result.add(new RolePermission(
                                        new Role(roleId),
                                        RSMapper.map(
                                                rs,
                                                Permission::new,
                                                rsMapper -> rsMapper
                                                        .mapIdBase()
                                                        .map(
                                                                "NAME",
                                                                Permission::setName,
                                                                PermissionType.class
                                                        )
                                                        .map("DESCRIPTION", Permission::setDescription)
                                                        .map("CDT", Permission::setCdt)
                                        ),
                                        rs.getInt("PERMISSION_ACCESS")
                                ));
                            }

                            return result;
                        }
                ).value(roleId)
                .execQuietly(dataSource);
    }

    @Override
    public List<PermissionDto> listAllByRoleIdAsModel(UUID roleId) {
        return SqlBuilder.select(
                        "SELECT " +
                                "p.ID," +
                                "p.NAME," +
                                "rp.PERMISSION_ACCESS " +
                                "FROM " + RolePermission.TABLE_NAME + " rp " +
                                "INNER JOIN " + Permission.TABLE_NAME + " p ON p.ID = rp.PERMISSION_ID " +
                                "WHERE RP.ROLE_ID = ?",
                        rs -> {
                            List<PermissionDto> result = new ArrayList<>();

                            while (rs.next()) {
                                try {
                                    result.add(new PermissionDto(
                                            UUID.fromString(rs.getString("ID")),
                                            PermissionType.fromName(rs.getString("NAME")),
                                            rs.getInt("PERMISSION_ACCESS")
                                    ));
                                } catch (IllegalArgumentException e) {
                                    log.error("listAllByRoleIdAsModel()", e);
                                }
                            }

                            return result;
                        }
                ).value(roleId)
                .execQuietly(dataSource);
    }
}
