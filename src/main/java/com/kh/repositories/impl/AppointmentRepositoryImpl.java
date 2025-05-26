package com.kh.repositories.impl;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.AppointmentRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class AppointmentRepositoryImpl extends AbstractRepository<Appointment, Long> implements AppointmentRepository {

    public AppointmentRepositoryImpl() {
        super(Appointment.class);
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
    public boolean isPatientTimeSlotTaken(User patient, Date date, AppointmentSlot slot) {
        Session session = getCurrentSession();

        String hql =
                "SELECT COUNT(a) FROM Appointment a " +
                        "WHERE a.patientId = :patientId " +
                        "AND a.appointmentDate = :date " +
                        "AND a.timeSlot = :slot " +
                        "AND a.status = :status ";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("patientId", patient)
                .setParameter("date", date)
                .setParameter("slot", slot)
                .setParameter("status", "scheduled")
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<AppointmentSlot> findDoctorTakenSlots(Long doctorId, Date date) {
        Session session = getCurrentSession();

        String hql =
                "SELECT a.timeSlot " +
                        "FROM Appointment a " +
                        "WHERE a.doctorId.id = :doctorId " +
                        "AND DATE(a.appointmentDate) = DATE(:date) " +
                        "AND a.status = 'scheduled' " +
                        "ORDER BY a.timeSlot ASC ";

        return session.createQuery(hql, AppointmentSlot.class)
                .setParameter("doctorId", doctorId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public List<AppointmentSlot> findPatientTakenSlots(Long patientId, Date date) {
        Session session = getCurrentSession();

        String hql =
                "SELECT a.timeSlot " +
                        "FROM Appointment a " +
                        "WHERE a.patientId.id = :patientId " +
                        "AND DATE(a.appointmentDate) = DATE(:date) " +
                        "AND a.status = 'scheduled' " +
                        "ORDER BY a.timeSlot ASC";

        return session.createQuery(hql, AppointmentSlot.class)
                .setParameter("patientId", patientId)
                .setParameter("date", date)
                .getResultList();
    }

    // Phương thức lấy danh sách lịch hẹn của bệnh nhân
    @Override
    public List<Appointment> findByPatientId(Long patientId, String status) {
        Session session = getCurrentSession();
        String hql = "FROM Appointment a WHERE a.patientId.id = :patientId " +
                (status != null ? "AND a.status = :status " : "") +
                "ORDER BY a.appointmentDate ASC, a.timeSlot ASC";

        Query<Appointment> query = session.createQuery(hql, Appointment.class)
                .setParameter("patientId", patientId);

        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }

    // Phương thức lấy danh sách lịch hẹn của bác sĩ
    @Override
    public List<Appointment> findByDoctorId(Long doctorId, String status) {
        Session session = getCurrentSession();
        String hql = "FROM Appointment a WHERE a.doctorId.id = :doctorId " +
                (status != null ? "AND a.status = :status " : "") +
                "ORDER BY a.appointmentDate ASC, a.timeSlot ASC";

        Query<Appointment> query = session.createQuery(hql, Appointment.class)
                .setParameter("doctorId", doctorId);

        if (status != null && !status.trim().isEmpty()) {
            query.setParameter("status", status);
        }

        return query.getResultList();
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
    public boolean existsCompletedAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId) {
        Session session = getCurrentSession();

        String hql = "SELECT COUNT(a.id) > 0 " +
                "FROM Appointment a " +
                "WHERE a.doctorId.id = :doctorId " +
                "AND a.patientId.id = :patientId " +
                "AND a.status = 'completed'";

        Query<Boolean> query = session.createQuery(hql, Boolean.class);
        query.setParameter("doctorId", doctorId);
        query.setParameter("patientId", patientId);

        return query.getSingleResult();
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

    @Override
    public List<Appointment> findAppointmentsBetweenDates(Date from, Date to) {
        Session session = getCurrentSession();

        String hql = "FROM Appointment a WHERE a.appointmentDate BETWEEN :from AND :to ORDER BY a.appointmentDate ASC";
        return session.createQuery(hql, Appointment.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public void updateExpiredAppointments() {
        Session session = getCurrentSession();
        String hql = """
                 UPDATE Appointment a
                 SET status = 'cancelled'
                 WHERE status = 'scheduled'
                             AND a.appointmentDate < CURRENT_DATE()
                             OR (a.appointmentDate =
                     CURRENT_DATE()\s
                 AND
                     FUNCTION('TIME',
                             CASE a.timeSlot
                         WHEN
                     'SLOT_1' THEN '07:30:00'
                         WHEN 'SLOT_2' THEN '08:00:00'
                         WHEN 'SLOT_3' THEN '08:30:00'
                         WHEN 'SLOT_4' THEN '09:00:00'
                         WHEN 'SLOT_5' THEN '09:30:00'
                         WHEN 'SLOT_6' THEN '10:00:00'
                         WHEN 'SLOT_7' THEN '10:30:00'
                         WHEN 'SLOT_8' THEN '13:00:00'
                         WHEN 'SLOT_9' THEN '13:30:00'
                         WHEN 'SLOT_10' THEN '14:00:00'
                         WHEN 'SLOT_11' THEN '14:30:00'
                         WHEN 'SLOT_12' THEN '15:00:00'
                         WHEN 'SLOT_13' THEN '15:30:00'
                         WHEN 'SLOT_14' THEN '16:00:00'
                         WHEN 'SLOT_15' THEN '16:30:00'
                         WHEN 'SLOT_16' THEN '17:00:00'
                     END) < CURRENT_TIME())
                
                \s""";

        session.createMutationQuery(hql)
                .executeUpdate();
    }


}