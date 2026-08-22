package io.github.adsa06.ormsql.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.adsa06.cbm.CustomBundleManager;
import io.github.adsa06.ormsql.repository.SimpleRepository;

public class DatabaseInitializer {

    private String jdbc;
    private CustomBundleManager bundle;

    public DatabaseInitializer(String jdbc, Dialect dialect) throws SQLException {
        this.jdbc = jdbc;
        bundle = new CustomBundleManager("dialect.dialect", dialect.getName());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbc);
    }

    public SimpleRepository getSimpleDao() throws SQLException {
        return new SimpleRepository(getConnection(), bundle);
    }
}
