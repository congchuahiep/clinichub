package com.kh.services.impl;

import com.kh.dtos.AppointmentDTO;
import com.kh.dtos.AppointmentDetailsDTO;
import com.kh.dtos.MedicalRecordDTO;
import com.kh.enums.AppointmentSlot;
import com.kh.enums.UserRole;
import com.kh.pojo.Appointment;
import com.kh.pojo.MedicalRecord;
import com.kh.pojo.User;
import com.kh.repositories.AppointmentRepository;
import com.kh.repositories.MedicalRecordRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.AppointmentService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;


    @Override
    public AppointmentDTO addAppointment(AppointmentDTO appointmentDTO, String patientUsername) {
        // Lấy thông tin bác sĩ và bệnh nhân
        User doctor = this.userRepository.
                findDoctorById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ!"));
        User patient = this.userRepository
                .findByUsername(patientUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại!"));

        AppointmentSlot timeSlot = AppointmentSlot.fromSlotNumber(appointmentDTO.getTimeSlot());

        // Kiểm tra lịch của bác sĩ
        if (appointmentRepository.isDoctorTimeSlotTaken(
                doctor,
                appointmentDTO.getAppointmentDate(),
                timeSlot
        )) {
            throw new IllegalArgumentException("Bác sĩ đã có lịch vào ca này.");
        }

        // Chuyển đổi DTO thành entity
        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctor);
        appointment.setPatientId(patient);
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setNote(appointmentDTO.getNote());
        appointment.setTimeSlot(timeSlot);
        appointment.setStatus("scheduled"); // Trạng thái mặc định khi tạo

        // Lưu vào database
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Chuyển entity thành DTO để trả về
        return new AppointmentDTO(savedAppointment);
    }


    @Override
    public List<AppointmentDTO> getAppointments(String username) {
        // Lấy thông tin người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại!"));

        // Lấy danh sách lịch hẹn theo vai trò
        List<Appointment> appointments;

        if (user.getRole() == UserRole.PATIENT) {
            // Nếu là bệnh nhân, lấy danh sách các lịch hẹn của bệnh nhân
            appointments = appointmentRepository.findByPatientId(user.getId());
        } else if (user.getRole() == UserRole.DOCTOR) {
            // Nếu là bác sĩ, lấy danh sách các lịch hẹn của bác sĩ
            appointments = appointmentRepository.findByDoctorId(user.getId());
        } else {
            // Nếu không phải là bệnh nhân hay bác sĩ, trả về lỗi
            throw new IllegalArgumentException("Chỉ bệnh nhân hoặc bác sĩ mới có thể truy cập lịch hẹn.");
        }

        // Chuyển danh sách appointment thành danh sách DTO
        return appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO getAppointmentDetails(Long appointmentId, String username)
            throws AccessDeniedException, NoSuchElementException {
        // Lấy chi tiết lịch khám từ repository
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy lịch khám với ID: " + appointmentId));

        // Kiểm tra quyền hợp lệ
        boolean isPatient = appointment.getPatientId().getUsername().equals(username);
        boolean isDoctor = appointment.getDoctorId().getUsername().equals(username);
        if (!isPatient && !isDoctor) {
            throw new AccessDeniedException("Bạn không có quyền truy cập lịch hẹn này.");
        }

        AppointmentDTO dto = new AppointmentDTO(appointment);

        MedicalRecord medicalRecord = appointment.getMedicalRecord();
        if (medicalRecord != null) {
            MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO(medicalRecord);
            dto.setMedicalRecord(medicalRecordDTO);
        }
        return dto;
    }

}