package com.kh.repositories.impl;

import com.kh.pojo.MedicalRecord;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.MedicalRecordRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MedicalRecordRepositoryImpl extends AbstractRepository implements MedicalRecordRepository {
    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        Session session = getCurrentSession();
        session.persist(medicalRecord);
        return medicalRecord;
    }
}
