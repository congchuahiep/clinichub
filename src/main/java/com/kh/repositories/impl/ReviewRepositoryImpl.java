package com.kh.repositories.impl;

import com.kh.pojo.Review;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.ReviewRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepositoryImpl extends AbstractRepository implements ReviewRepository {
    @Override
    public Review save(Review review) {
        Session session = this.getCurrentSession();
        session.persist(review);
        return review;
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
    public List<Review> doctorReviewList(Long doctorId, int page, int pageSize) {
        Session session = this.getCurrentSession();

        String hql = "FROM Review r " +
                "LEFT JOIN FETCH r.patientId " +
                "WHERE r.doctorId.id = :doctorId " +
                "ORDER BY r.createdAt DESC";

        Query<Review> query = session.createQuery(hql, Review.class);

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        query.setParameter("doctorId", doctorId);

        return query.getResultList();
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
