package io.github.adsa06.ormsql;

public enum Dialect {
    SQLITE("sqlite"),
    MYSQL("mysql");

    private final String name;

    Dialect(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
