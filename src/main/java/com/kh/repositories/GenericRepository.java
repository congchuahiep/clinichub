package com.kh.repositories;

import com.kh.utils.PaginatedResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    T save(T entity);

    T update(T entity);

    Optional<T> findById(ID id);

    void delete(T entity);

    void deleteById(ID id);

    List<T> list();

    PaginatedResult<T> paginatedList(Map<String, String> params);
}
