package io.github.adsa06.ormsql.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.adsa06.cbm.CustomBundleManager;
import io.github.adsa06.ormsql.mapping.annotation.Column;
import io.github.adsa06.ormsql.mapping.annotation.Table;

public class SimpleRepository implements Repository {

    private Connection conn;
    private CustomBundleManager bundle;

    public SimpleRepository(Connection connection, CustomBundleManager bundle) {
        this.conn = connection;
        this.bundle = bundle;
    }

    private String resolveColumnName(Field field) {
        String columnName = field.getAnnotation(Column.class).name(); // Default: ""
        return columnName.length() > 0 ? columnName : field.getName();
    }

    // Falta mejor manejo de excepciones y manejo de herencia
    @Override
    public <T> List<T> findAll(Class<T> type) throws NoSuchMethodException, SecurityException {
        List<T> entities = new ArrayList<>();

        if (!type.isAnnotationPresent(Table.class))
            throw new IllegalStateException("Class " + type.getName() + " is not annotated with @Table");

        String tableName = type.getAnnotation(Table.class).name();

        List<Field> fields = List.of(type.getDeclaredFields()).stream()
                .filter(f -> f.isAnnotationPresent(Column.class))
                .filter(f -> !Modifier.isStatic(f.getModifiers())) // para static y cosas asi
                .toList();

        String columns = fields.stream()
                .map(this::resolveColumnName)
                .collect(Collectors.joining(", "));

        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(bundle.getString("select", columns, tableName))) {

            while (rs.next()) {
                T instance = constructor.newInstance();

                fields.stream().forEach(f -> {
                    Method method;
                    try {
                        String methodName = "set" + f.getName().substring(0, 1).toUpperCase()
                                + f.getName().substring(1);
                        method = type.getMethod(methodName, f.getType());
                        method.setAccessible(true);

                        method.invoke(instance, rs.getObject(resolveColumnName(f)));
                    } catch (NoSuchMethodException
                            | SecurityException
                            | IllegalAccessException
                            | InvocationTargetException
                            | SQLException e) {
                        e.printStackTrace();
                    }
                });

                entities.add(instance);
            }

        } catch (SQLException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        return entities;
    }

    @Override
    public <T> List<T> saveAll(List<T> entitys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
    }

    @Override
    public <T> T save(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public <T> boolean deleteAll(List<T> entitys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

    @Override
    public <T> boolean delete(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public <T> List<T> updateAll(List<T> entitys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAll'");
    }

    @Override
    public <T> T update(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
