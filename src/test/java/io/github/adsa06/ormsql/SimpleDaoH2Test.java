package io.github.adsa06.ormsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.adsa06.ormsql.config.DatabaseInitializer;
import io.github.adsa06.ormsql.config.Dialect;
import io.github.adsa06.ormsql.exception.ObjectRelationMappingException;
import io.github.adsa06.ormsql.mapping.annotation.Column;
import io.github.adsa06.ormsql.mapping.annotation.Table;
import io.github.adsa06.ormsql.repository.SimpleRepository;

class SimpleDaoH2Test {

    private static final String JDBC_URL = "jdbc:h2:mem:ormsql_test;DB_CLOSE_DELAY=-1";

    @BeforeEach
    void setUpDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS users");
            statement.executeUpdate("CREATE TABLE users (user_id INT, display_name VARCHAR(100))");
            statement.executeUpdate("INSERT INTO users (user_id, display_name) VALUES (1, 'Ada')");
            statement.executeUpdate("INSERT INTO users (user_id, display_name) VALUES (2, 'Grace')");
        }
    }

    @Test
    void findAllMapsH2RowsToAnnotatedEntity() throws SQLException, ObjectRelationMappingException {
        SimpleRepository repository = new DatabaseInitializer(JDBC_URL, Dialect.H2).getSimpleDao();

        List<User> users = repository.find(User.class);

        assertEquals(2, users.size());
        assertEquals(new User(1, "Ada"), users.get(0));
        assertEquals(new User(2, "Grace"), users.get(1));
    }

    @Test
    void findAllUsesFieldNameWhenColumnNameIsEmpty() throws SQLException, ObjectRelationMappingException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("CREATE TABLE users (id INT, name VARCHAR(100))");
            statement.executeUpdate("INSERT INTO users (id, name) VALUES (7, 'Linus')");
        }

        SimpleRepository repository = new DatabaseInitializer(JDBC_URL, Dialect.H2).getSimpleDao();
        List<UserWithDefaultColumns> users = repository.find(UserWithDefaultColumns.class);

        assertEquals(List.of(new UserWithDefaultColumns(7, "Linus")), users);
    }

    @Test
    void findAllRejectsEntityWithoutTableAnnotation() throws SQLException, ObjectRelationMappingException {
        SimpleRepository repository = new DatabaseInitializer(JDBC_URL, Dialect.H2).getSimpleDao();

        assertThrows(IllegalStateException.class, () -> repository.find(UnmappedUser.class));
    }

    @Table(name = "users")
    static class User {
        @Column(name = "user_id")
        private int id;

        @Column(name = "display_name")
        private String name;

        User() {
        }

        User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof User user)) {
                return false;
            }
            return id == user.id && name.equals(user.name);
        }
    }

    @Table(name = "users")
    static class UserWithDefaultColumns {
        @Column(name = "")
        private int id;

        @Column
        private String name;

        UserWithDefaultColumns() {
        }

        UserWithDefaultColumns(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof UserWithDefaultColumns user)) {
                return false;
            }
            return id == user.id && name.equals(user.name);
        }
    }

    static class UnmappedUser {
        UnmappedUser() {
        }
    }
}
