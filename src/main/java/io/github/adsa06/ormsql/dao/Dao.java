package io.github.adsa06.ormsql.dao;

import java.util.List;

public interface Dao {
    <T> List<T> findAll(Class<T> type) throws NoSuchMethodException, SecurityException;
    //<T> T find(Class<T> type, String column, String condition);

    <T> List<T> saveAll(List<T> entitys);
    <T> T save(T entity);

    <T> boolean deleteAll(List<T> entitys);
    <T> boolean delete(T entity);

    <T> List<T> updateAll(List<T> entitys);
    <T> T update(T entity);
}
