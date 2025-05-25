package com.kh.services;

import com.kh.dtos.AppointmentDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

public interface AppointmentService {
    AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername);

    List<AppointmentDTO> getAppointments(String username, String status);

    AppointmentDTO getAppointmentDetails(Long appointmentId, String username) throws AccessDeniedException, NoSuchElementException;

}