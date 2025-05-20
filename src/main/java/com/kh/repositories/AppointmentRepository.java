package com.kh.repositories;

import com.kh.enums.AppointmentSlot;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;

import java.util.Date;

public interface AppointmentRepository {
    Appointment add(Appointment appointment);

    boolean isDoctorTimeSlotTaken(User doctor, Date date, AppointmentSlot slot);
}