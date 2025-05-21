package com.kh.repositories.impl;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.AppointmentRepository;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class AppointmentRepositoryImpl extends AbstractRepository implements AppointmentRepository {

    public AppointmentRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Override
    public Appointment add(Appointment appointment) {
        Session session = getCurrentSession();
        session.persist(appointment);
        return appointment;
    }

    @Override
    public boolean isDoctorTimeSlotTaken(User doctor, Date date, AppointmentSlot slot) {
        Session session = getCurrentSession();

        String hql =
                "SELECT COUNT(a) FROM Appointment a " +
                        "WHERE a.doctorId = :doctorId " +
                        "AND a.appointmentDate = :date " +
                        "AND a.timeSlot = :slot " +
                        "AND a.status = :status ";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("doctorId", doctor)
                .setParameter("date", date)
                .setParameter("slot", slot)
                .setParameter("status", "scheduled")
                .getSingleResult();

        return count > 0;
    }
    
    
    // Phương thức lấy danh sách lịch hẹn của bệnh nhân
    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        Session session = getCurrentSession();
        String hql = "FROM Appointment a WHERE a.patientId.id = :patientId";
        return session.createQuery(hql, Appointment.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    // Phương thức lấy danh sách lịch hẹn của bác sĩ
    @Override
    public List<Appointment> findByDoctorId(Long doctorId) {
        Session session = getCurrentSession();
        String hql = "FROM Appointment a WHERE a.doctorId.id = :doctorId";
        return session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId)
                .getResultList();
    }
}