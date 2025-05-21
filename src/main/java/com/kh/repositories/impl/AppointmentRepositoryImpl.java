package com.kh.repositories.impl;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.AppointmentRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AppointmentRepositoryImpl extends AbstractRepository implements AppointmentRepository {

    public AppointmentRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Override
    public Appointment save(Appointment appointment) {
        Session session = getCurrentSession();

        if (appointment.getId() == null) {
            session.persist(appointment);
        } else {
            session.merge(appointment);
        }

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


    @Override
    public Optional<Appointment> findById(Long id) {
        Session session = getCurrentSession();
        Query<Appointment> query = session.createQuery(
                "FROM Appointment WHERE id = :id",
                Appointment.class
        );

        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
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

    @Override
    public boolean existsAppointmentMedicalRecord(Long appointmentId) {
        Session session = getCurrentSession();
        String hql = "SELECT COUNT(a) FROM Appointment a WHERE a.id = :appointmentId AND a.medicalRecord IS NOT NULL";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("appointmentId", appointmentId)
                .getSingleResult();

        return count > 0;
    }
}