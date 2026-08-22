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
import io.github.adsa06.ormsql.exception.ObjectRelationMappingException;
import io.github.adsa06.ormsql.mapping.annotation.Column;
import io.github.adsa06.ormsql.mapping.annotation.Table;
import io.github.adsa06.ormsql.query.predicate.Predicate;

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

    // Falta manejo de herencia
    @Override
    public <T> List<T> find(Class<T> type) throws ObjectRelationMappingException {
        List<T> entities = new ArrayList<>();

        if (!type.isAnnotationPresent(Table.class))
            throw new IllegalStateException("Class " + type.getName() + " is not annotated with @Table");

        String tableName = type.getAnnotation(Table.class).name();

        List<Field> fields = List.of(type.getDeclaredFields()).stream()
                .filter(f -> f.isAnnotationPresent(Column.class))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .toList();

        String columns = fields.stream()
                .map(this::resolveColumnName)
                .collect(Collectors.joining(", "));

        Constructor<T> constructor;
        try {
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ObjectRelationMappingException("Cannot access an empty constructor");
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(bundle.getString("select", columns, tableName))) {

            while (rs.next()) {
                T instance = constructor.newInstance();

                for (Field f : fields) {
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
                        throw new ObjectRelationMappingException("Cannot set field");
                    }
                }
                entities.add(instance);
            }

        } catch (SQLException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ObjectRelationMappingException("Cannot instantiate the object");
        }

        return entities;
    }

    @Override
    public <T> List<T> find(Class<T> type, Predicate predicate) throws ObjectRelationMappingException {
        List<T> entities = new ArrayList<>();

        if (!type.isAnnotationPresent(Table.class))
            throw new IllegalStateException("Class " + type.getName() + " is not annotated with @Table");

        String tableName = type.getAnnotation(Table.class).name();

        List<Field> fields = List.of(type.getDeclaredFields()).stream()
                .filter(f -> f.isAnnotationPresent(Column.class))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .toList();

        String columns = fields.stream()
                .map(this::resolveColumnName)
                .collect(Collectors.joining(", "));

        Constructor<T> constructor;
        try {
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ObjectRelationMappingException("Cannot access an empty constructor");
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery(bundle.getString("selectWhere", columns, tableName, predicate.toSql(bundle)))) {

            while (rs.next()) {
                T instance = constructor.newInstance();

                for (Field f : fields) {
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
                        throw new ObjectRelationMappingException("Cannot set field");
                    }
                }
                entities.add(instance);
            }

        } catch (SQLException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new ObjectRelationMappingException("Cannot instantiate the object");
        }

        return entities;
    }

    @Override
    public <T> List<T> save(List<T> entitys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public <T> T save(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public <T> int delete(Class<T> type, Predicate predicate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public <T> boolean delete(List<T> entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public <T> boolean delete(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public <T> int update(Class<T> type, Predicate predicate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public <T> List<T> update(List<T> entitys) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public <T> T update(T entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
