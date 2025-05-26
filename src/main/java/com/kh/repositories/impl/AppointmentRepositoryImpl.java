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

    public Long countDistinctPatientsCompletedByMonth(int year, int month) {
        Session session = getCurrentSession();

        String hql = "SELECT COUNT(DISTINCT a.patientId) " +
                "FROM Appointment a " +
                "WHERE a.status = :status " +
                "AND year(a.appointmentDate) = :year " +
                "AND month(a.appointmentDate) = :month";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("status", "completed");
        query.setParameter("year", year);
        query.setParameter("month", month);

        return query.uniqueResultOptional().orElse(0L);
    }

    public Long countDistinctPatientsCompletedByQuarter(int year, int quarter) {
        Session session = getCurrentSession();

        int startMonth = (quarter - 1) * 3 + 1; // quý 1 -> 1, quý 2 -> 4, quý 3 -> 7, quý 4 -> 10
        int endMonth = startMonth + 2;

        String hql = "SELECT COUNT(DISTINCT a.patientId) " +
                "FROM Appointment a " +
                "WHERE a.status = :status " +
                "AND year(a.appointmentDate) = :year " +
                "AND month(a.appointmentDate) BETWEEN :startMonth AND :endMonth";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("status", "completed");
        query.setParameter("year", year);
        query.setParameter("startMonth", startMonth);
        query.setParameter("endMonth", endMonth);

        return query.uniqueResultOptional().orElse(0L);
    }

    public Long countDistinctPatientsCompleted() {
        Session session = getCurrentSession();

        String hql = "SELECT COUNT(DISTINCT a.patientId) " +
                "FROM Appointment a " +
                "WHERE a.status = :status";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("status", "completed");

        return query.uniqueResultOptional().orElse(0L);
    }

}