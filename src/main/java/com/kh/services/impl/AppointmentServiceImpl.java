package com.kh.services.impl;

import com.kh.dtos.AppointmentDTO;
import com.kh.dtos.UserDTO;
import com.kh.pojo.Appointment;
import com.kh.pojo.User;
import com.kh.repositories.AppointmentRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername) {
        // Lấy thông tin bác sĩ và bệnh nhân
        User doctor = userRepository.getUserById(appointmentDTO.getDoctorId());
        User patient = userRepository.getUserByUsername(patientUsername);
        
        if (doctor == null)
            throw new RuntimeException("Không tìm thấy bác sĩ!");

        // Chuyển đổi DTO thành entity
        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setAppointmentDatetime(appointmentDTO.getAppointmentDatetime());
        appointment.setNote(appointmentDTO.getNote());
        appointment.setStatus("scheduled"); // Trạng thái mặc định khi tạo
        
        // Lưu vào database
        Appointment savedAppointment = appointmentRepository.add(appointment);
        
        // Chuyển entity thành DTO để trả về
        return new AppointmentDTO(savedAppointment);
    }
}