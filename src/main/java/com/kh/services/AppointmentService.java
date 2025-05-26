package com.kh.services;

import com.kh.dtos.AppointmentDTO;
import com.kh.enums.AppointmentSlot;
import jakarta.transaction.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public interface AppointmentService {

    List<AppointmentSlot> findTakenSlots(Long patientId, Long doctorId, Date date);

    AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername);

    List<AppointmentDTO> getAppointments(String username, String status);

    AppointmentDTO getAppointmentDetails(Long appointmentId, String username) throws AccessDeniedException, NoSuchElementException;

    void cancelAppointment(Long appointmentId, String username)
            throws AccessDeniedException, NoSuchElementException, IllegalStateException;
}