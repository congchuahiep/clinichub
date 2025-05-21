package com.kh.repositories.impl;

import com.kh.pojo.HealthRecord;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.HealthRecordRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Session;
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
    public List<HealthRecord> findAll() {
        Session session = getCurrentSession();

        String hql = "SELECT h FROM HealthRecord h";
        Query<HealthRecord> query = session.createQuery(hql, HealthRecord.class);

        return query.getResultList();
    }

    @Override
    public HealthRecord save(HealthRecord healthRecord) {
        Session session = getCurrentSession();

        // ID của HealthRecord giờ sẽ là ID của User
        if (healthRecord.getPatient() != null) {
            healthRecord.setPatient(healthRecord.getPatient());
        }

        return session.merge(healthRecord);
    }


    @Override
    public void delete(Long id) {
        Session session = getCurrentSession();
        findById(id).ifPresent(session::remove);
    }
}
