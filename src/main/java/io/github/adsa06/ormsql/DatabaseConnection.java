package io.github.adsa06.ormsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.adsa06.cbm.CustomBundleManager;
import io.github.adsa06.ormsql.dao.SimpleDao;

public class DatabaseConnection {

    private String jdbc;
    private CustomBundleManager bundle;

    public DatabaseConnection(String jdbc, Dialect dialect) throws SQLException {
        this.jdbc = jdbc;
        bundle = new CustomBundleManager("statement.statement", dialect.getName());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbc);
    }

    public SimpleDao getSimpleDao() throws SQLException {
        return new SimpleDao(getConnection(), bundle);
    }
}
