package com.kh.repositories.impl;

import com.kh.pojo.HealthRecord;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.HealthRecordRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class HealthRecordRepositoryImpl extends AbstractRepository implements HealthRecordRepository {

    public HealthRecordRepositoryImpl(LocalSessionFactoryBean sessionFactory) {
        this.factory = sessionFactory;
    }

    @Override
    public Optional<HealthRecord> findById(Long id) {
        Session session = getCurrentSession();

        try {
            return Optional.ofNullable(session.get(HealthRecord.class, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<HealthRecord> findByPatientId(Long patientId) {
        Session session = getCurrentSession();

        String hql = "SELECT h FROM HealthRecord h WHERE h.patientId.id = :patientId";
        Query<HealthRecord> query = session.createQuery(hql, HealthRecord.class);
        query.setParameter("patientId", patientId);

        try {
            return Optional.of(query.getResultList().get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<HealthRecord> findAll() {
        Session session = getCurrentSession();

        String hql = "SELECT h FROM HealthRecord h";
        Query<HealthRecord> query = session.createQuery(hql, HealthRecord.class);

        return query.getResultList();
    }

    @Override
    public HealthRecord save(HealthRecord healthRecord) {
        Session session = getCurrentSession();

        if (healthRecord.getId() == null) {
            session.persist(healthRecord);
            return healthRecord;
        } else {
            return session.merge(healthRecord);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = getCurrentSession();
        findById(id).ifPresent(session::remove);
    }

    @Override
    public boolean existsAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId) {
        Session session = getCurrentSession();
        String hql =
                "SELECT COUNT(a) " +
                        "FROM Appointment a " +
                        "WHERE a.doctorId.id = :doctorId AND a.patientId.id = :patientId " +
                        "AND a.status IN ('scheduled', 'completed', 'rescheduled')";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("doctorId", doctorId)
                .setParameter("patientId", patientId)
                .getSingleResult();
        return count != null && count > 0;
    }
}
