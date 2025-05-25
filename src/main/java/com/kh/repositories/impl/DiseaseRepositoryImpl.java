package com.kh.repositories.impl;

import java.util.*;

import com.kh.utils.PaginatedResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.pojo.Disease;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.DiseaseRepository;

@Repository
@Transactional
public class DiseaseRepositoryImpl extends AbstractRepository<Disease, Long> implements DiseaseRepository {

    public DiseaseRepositoryImpl() {
        super(Disease.class);
    }

    public PaginatedResult<Disease> paginatedList(Map<String, String> params) {
        Session session = getCurrentSession();

        // Base queries
        StringBuilder selectHql = new StringBuilder("FROM Disease d ");
        StringBuilder countHql = new StringBuilder("SELECT COUNT(d) FROM Disease d ");
        StringBuilder whereClause = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        // Xử lý điều kiện tìm kiếm theo tên
        if (params != null && params.containsKey("kw") && !params.get("kw").trim().isEmpty()) {
            String searchName = params.get("kw").trim();
            whereClause.append("WHERE LOWER(d.name) LIKE :name ");
            parameters.put("name", "%" + searchName.toLowerCase() + "%");
        }

        // Thêm where clause vào câu query
        selectHql.append(whereClause);
        countHql.append(whereClause);

        // Order by
        selectHql.append("ORDER BY d.name ASC");

        // Tạo và setup queries
        Query<Disease> selectQuery = session.createQuery(selectHql.toString(), Disease.class);
        Query<Long> countQuery = session.createQuery(countHql.toString(), Long.class);

        // Đặt các tham số cho cả 2 queries
        parameters.forEach((key, value) -> {
            selectQuery.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        // Xử lý phân trang
        int page = 1;
        int pageSize = 10;

        if (params != null) {
            page = Integer.parseInt(params.getOrDefault("page", "1"));
            pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10"));
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
        }

        // Thực thi queries
        List<Disease> elements = selectQuery.getResultList();
        Long totalElements = countQuery.getSingleResult();

        return new PaginatedResult<>(elements, page, pageSize, totalElements);
    }


}
