package io.github.adsa06.ormsql;

public enum Dialect {
    SQLITE("sqlite"),
    H2("h2"),
    MYSQL("mysql");

    private final String name;

    Dialect(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
