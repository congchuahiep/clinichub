package com.kh.repositories.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class DiseaseRepositoryImpl extends AbstractRepository implements DiseaseRepository {

    private static final int PAGE_SIZE = 20;

    public DiseaseRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Override
    public List<Disease> getDiseaseList(Map<String, String> params) {
        Session session = getCurrentSession();

        Query<Disease> q = session.createQuery("FROM Disease", Disease.class);

        int page = NumberUtils.toInt(params.get("page"), 1);

        q.setFirstResult((page - 1) * PAGE_SIZE);
        q.setMaxResults(PAGE_SIZE);

        return q.getResultList();
    }

    @Override
    public Optional<Disease> findById(Long id) {
        Session session = getCurrentSession();

        Query<Disease> query = session.createQuery(
                "FROM Disease WHERE id = :id",
                Disease.class
        );

        query.setParameter("id", id);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
