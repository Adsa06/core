package io.github.adsa06.ormsql.repository;

import java.util.List;

import io.github.adsa06.ormsql.exception.ObjectRelationMappingException;
import io.github.adsa06.ormsql.query.predicate.Predicate;

public interface Repository {
    <T> List<T> find(Class<T> type) throws ObjectRelationMappingException;
    <T> List<T> find(Class<T> type, Predicate predicate) throws ObjectRelationMappingException;

    <T> List<T> save(List<T> entitys);
    <T> T save(T entity);

    <T> int delete(Class<T> type, Predicate predicate);
    <T> boolean delete(List<T> entity);
    <T> boolean delete(T entity);

    <T> int update(Class<T> type, Predicate predicate);
    <T> List<T> update(List<T> entitys);
    <T> T update(T entity);
}
