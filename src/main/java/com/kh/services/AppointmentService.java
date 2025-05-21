package com.kh.services;

import com.kh.dtos.AppointmentDTO;
import java.util.List;

public interface AppointmentService {
    AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername);
    List<AppointmentDTO> getAppointments(String username);
}