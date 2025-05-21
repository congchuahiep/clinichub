package com.kh.repositories;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository {
    Appointment add(Appointment appointment);

    boolean isDoctorTimeSlotTaken(User doctor, Date date, AppointmentSlot slot);
    
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
}