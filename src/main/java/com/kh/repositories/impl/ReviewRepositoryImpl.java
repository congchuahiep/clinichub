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

//        return this.executeListQuery(this, session, builder, criteriaQuery, root, params);
        return null;
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
}
