package com.parser.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JpaConfig extends HikariConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasourceJdbc.cachePrepStmts}")
    private String cachePrepStmts;
    @Value("${spring.datasourceJdbc.prepStmtCacheSize}")
    private String prepStmtCacheSize;
    @Value("${spring.datasourceJdbc.prepStmtCacheSqlLimit}")
    private String prepStmtCacheSqlLimit;
    @Value("${spring.datasourceJdbc.maximumPoolSize}")
    private String maximumPoolSize;
    @Value("${spring.datasourceJdbc.connectionTimeout}")
    private String connectionTimeout;
    @Value("${spring.datasourceJdbc.idleTimeout}")
    private String idleTimeout;
    @Value("${spring.datasourceJdbc.maxLifetime}")
    private String maxLifetime;

    @Bean
    public DataSource dataSource() {
        this.setJdbcUrl(url);
        this.setUsername(username);
        this.setPassword(password);
        this.setDriverClassName(driverClassName);
        this.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        this.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        this.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        this.addDataSourceProperty("maximumPoolSize", maximumPoolSize);
        this.addDataSourceProperty("connectionTimeout", connectionTimeout);
        this.addDataSourceProperty("idleTimeout", idleTimeout);
        this.addDataSourceProperty("maxLifetime", maxLifetime);
        return new HikariDataSource(this);
    }

}