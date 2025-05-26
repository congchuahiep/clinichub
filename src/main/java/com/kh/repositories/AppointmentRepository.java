package com.kh.repositories;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends GenericRepository<Appointment, Long> {

    List<AppointmentSlot> findDoctorTakenSlots(Long doctorId, Date date);

    List<AppointmentSlot> findPatientTakenSlots(Long patientId, Date date);

    List<Appointment> findByPatientId(Long patientId, String status);

    List<Appointment> findByDoctorId(Long doctorId, String status);

    boolean isDoctorTimeSlotTaken(User doctor, Date date, AppointmentSlot slot);

    boolean isPatientTimeSlotTaken(User patient, Date date, AppointmentSlot slot);

    boolean existsAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId);

    boolean existsCompletedAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId);

    boolean existsAppointmentMedicalRecord(Long appointmentId);

    List<Appointment> findAppointmentsBetweenDates(Date from, Date to);

    void updateExpiredAppointments();
}