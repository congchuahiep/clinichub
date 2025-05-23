package com.kh.repositories.impl;

import com.kh.pojo.Hospital;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.HospitalRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class HospitalRepositoryImpl extends AbstractRepository implements HospitalRepository {

    public HospitalRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Hospital> findById(Long id) {
        Session session = this.getCurrentSession();
        Query<Hospital> query = session.createQuery("FROM Hospital WHERE id = :id", Hospital.class);
        query.setParameter("id", id);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Hospital> list() {
        Session session = this.getCurrentSession();
        Query<Hospital> query = session.createQuery("FROM Hospital", Hospital.class);

        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void registerDoctorToHospital(Hospital hospital, User doctor) {
        Session session = this.getCurrentSession();

        // Lưu thông tin bệnh viện của bác sĩ
        if (hospital.getUserSet() == null) {
            hospital.setUserSet(new HashSet<>());
        }
        hospital.getUserSet().add(doctor);
        session.merge(hospital);
    }
}
