package com.kh.repositories.impl;

import com.kh.pojo.Review;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.ReviewRepository;
import com.kh.utils.PaginatedResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class ReviewRepositoryImpl extends AbstractRepository<Review, Long> implements ReviewRepository {

    public ReviewRepositoryImpl() {
        super(Review.class);
    }

    @Override
    public boolean existsReviewByDoctorAndPatient(Long doctorId, Long patientId) {
        Session session = this.getCurrentSession();

        String hql = "SELECT COUNT(r) > 0 FROM Review r " +
                "WHERE r.doctorId.id = :doctorId " +
                "AND r.patientId.id = :patientId";

        Query<Boolean> query = session.createQuery(hql, Boolean.class);;

        query.setParameter("doctorId", doctorId);
        query.setParameter("patientId", patientId);

        return query.getSingleResult();
    }

    @Override
    public PaginatedResult<Review> doctorReviewList(Long doctorId, Map<String, String> params) {
        Session session = this.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> criteriaQuery = builder.createQuery(Review.class);
        Root<Review> root = criteriaQuery.from(Review.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("doctorId").get("id"), doctorId));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        int page = 1;
        int pageSize = 10;

        if (params != null) {
            page = Integer.parseInt(params.getOrDefault("page", "1"));
            pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10"));
        }

        Query<Review> query = session.createQuery(criteriaQuery);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<Review> reviews = query.getResultList();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Review> countRoot = countQuery.from(Review.class);
        countQuery.select(builder.count(countRoot));
        countQuery.where(builder.equal(countRoot.get("doctorId").get("id"), doctorId));

        Long totalElements = session.createQuery(countQuery).getSingleResult();

        return new PaginatedResult<>(reviews, page, pageSize, totalElements);
    }

    @Override
    public Long countDoctorReview(Long doctorId) {
        Session session = this.getCurrentSession();

        String hql = "SELECT COUNT(r) FROM Review r " +
                "WHERE r.doctorId.id = :doctorId";

        Query<Long> query = session.createQuery(hql, Long.class);

        query.setParameter("doctorId", doctorId);

        return query.getSingleResult();
    }

    @Override
    public Double calculateAverageRatingByDoctor(Long doctorId) {
        Session session = this.getCurrentSession();

        String hql = "SELECT AVG(r.rating) FROM Review r " +
                "WHERE r.doctorId.id = :doctorId";

        Query<Double> query = session.createQuery(hql, Double.class);
        query.setParameter("doctorId", doctorId);

        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }

}
