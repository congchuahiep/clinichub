package com.kh.repositories.impl;

import com.kh.pojo.Appointment;
import com.kh.pojo.MedicalRecord;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.MedicalRecordRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class MedicalRecordRepositoryImpl extends AbstractRepository<MedicalRecord, Appointment> implements MedicalRecordRepository {

    public MedicalRecordRepositoryImpl() {
        super(MedicalRecord.class);
    }

    public List<MedicalRecord> findByPatientId(Long patientId, int page, int pageSize) {
        Session session = getCurrentSession();
        String hql = "FROM MedicalRecord WHERE healthRecordId.patient.id = :id ORDER BY createdAt DESC";
        Query<MedicalRecord> query = session.createQuery(hql, MedicalRecord.class);

        query.setParameter("id", patientId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        return Optional.ofNullable(query.getResultList()).orElse(List.of());
    }

    @Override
    public Long countByPatientId(Long patientId) {
        Session session = getCurrentSession();
        String hql = "SELECT COUNT(m) FROM MedicalRecord m " +
                "WHERE m.healthRecordId.patient.id = :id";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("id", patientId);
        return query.getSingleResult();
    }
}
