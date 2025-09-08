package com.parser.core.config;

import com.parser.core.auth.permission.service.PermissionServiceImpl;
import com.parser.core.util.dao.Closer;
import jakarta.annotation.PostConstruct;
import liquibase.exception.DatabaseException;
import liquibase.exception.LockException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.integration.spring.SpringResourceAccessor;
import liquibase.lockservice.LockService;
import liquibase.lockservice.LockServiceFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LiquibaseConfig extends SpringLiquibase {

    PermissionServiceImpl permissionService;
    Boolean enabled;

    LiquibaseConfig(
            DataSource dataSource,
            PermissionServiceImpl permissionService,
            @Value("${spring.liquibase.change-log}") String changeLog,
            @Value("${spring.liquibase.enabled}") Boolean enabled
    ) {
        this.enabled = enabled;
        this.permissionService = permissionService;

        setChangeLog("classpath:" + changeLog);
        setDataSource(dataSource);
    }

    @PostConstruct
    void init() throws SQLException, DatabaseException, LockException {
        Closer closer = new Closer();
        SpringResourceAccessor resourceAccessor = createResourceOpener();
        var connection = closer.reg(dataSource.getConnection());
        LockService lockService = LockServiceFactory.getInstance()
                .getLockService(createDatabase(connection, resourceAccessor));
        lockService.waitForLock();
        closer.reg(lockService::releaseLock);

        try {
            if (enabled)
                initEnums();
        } finally {
            closer.close();
        }
    }

    private void initEnums() {
        permissionService.initPermissions();
    }

}