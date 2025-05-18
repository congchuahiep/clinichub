package com.kh.services;

import com.kh.dtos.AppointmentDTO;

public interface AppointmentService {
    AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername);
}