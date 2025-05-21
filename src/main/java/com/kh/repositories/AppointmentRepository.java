package com.kh.repositories;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long id);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    boolean isDoctorTimeSlotTaken(User doctor, Date date, AppointmentSlot slot);

    boolean existsAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId);

    boolean existsAppointmentMedicalRecord(Long appointmentId);
}