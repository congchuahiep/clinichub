package com.kh.repositories.impl;

import com.kh.pojo.Review;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.ReviewRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

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
}
