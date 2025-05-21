package com.kh.repositories.impl;

import com.kh.pojo.HealthRecord;
import com.kh.repositories.HealthRecordRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class HealthRecordRepositoryImpl implements HealthRecordRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<HealthRecord> findById(Long id) {
        return Optional.ofNullable(em.find(HealthRecord.class, id));
    }

    @Override
    public Optional<HealthRecord> findByPatientId(Long patientId) {
        String jpql = "SELECT h FROM HealthRecord h WHERE h.patientId.id = :patientId";
        TypedQuery<HealthRecord> query = em.createQuery(jpql, HealthRecord.class);
        query.setParameter("patientId", patientId);
        List<HealthRecord> list = query.getResultList();
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    @Override
    public List<HealthRecord> findAll() {
        String jpql = "SELECT h FROM HealthRecord h";
        return em.createQuery(jpql, HealthRecord.class).getResultList();
    }

    @Override
    public HealthRecord save(HealthRecord healthRecord) {
        if (healthRecord.getId() == null) {
            em.persist(healthRecord);
            return healthRecord;
        } else {
            return em.merge(healthRecord);
        }
    }

    @Override
    public void delete(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public boolean existsAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId) {
        String hql =
                "SELECT COUNT(a) " +
                        "FROM Appointment a " +
                        "WHERE a.doctorId.id = :doctorId AND a.patientId.id = :patientId " +
                        "AND a.status IN ('scheduled', 'completed', 'rescheduled')";

        Long count = em.createQuery(hql, Long.class)
                .setParameter("doctorId", doctorId)
                .setParameter("patientId", patientId)
                .getSingleResult();
        return count != null && count > 0;
    }
}
