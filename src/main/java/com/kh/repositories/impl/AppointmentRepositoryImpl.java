package com.kh.repositories.impl;

import com.kh.pojo.Appointment;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.AppointmentRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AppointmentRepositoryImpl extends AbstractRepository implements AppointmentRepository {
    @Override
    public Appointment add(Appointment appointment) {
        Session session = getCurrentSession();
        session.persist(appointment);
        return appointment;
    }
}