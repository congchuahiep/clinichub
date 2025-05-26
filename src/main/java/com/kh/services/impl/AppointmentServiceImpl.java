package com.kh.services.impl;

import com.kh.dtos.AppointmentDTO;
import com.kh.dtos.EmailDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.kh.services.EmailService;
import com.kh.utils.DateUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private EmailService emailService;


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

        sendAppointmentEmailToPatient(patient, doctor, savedAppointment);
        sendAppointmentEmailToDoctor(patient, doctor, savedAppointment);


        // Chuyển entity thành DTO để trả về
        return new AppointmentDTO(savedAppointment);
    }

    private void sendAppointmentEmailToPatient(User patient, User doctor, Appointment appointment) {
        EmailDTO email = new EmailDTO();
        String formattedDate = DateUtils.formatVietnameseDate(appointment.getAppointmentDate());

        email.setToEmail(patient.getEmail());
        email.setSubject("Xác nhận lịch hẹn khám với bác sĩ " + doctor.getFirstName() + " " + doctor.getLastName());

        String body = String.format(
                "Kính chào %s %s,\n\n" +
                        "Bạn đã đặt lịch hẹn thành công với bác sĩ %s %s.\n\n" +
                        "🗓 Ngày khám: %s\n" +
                        "⏰ Ca khám: %s\n" +
                        "📝 Ghi chú: %s\n\n" +
                        "Vui lòng đến sớm 15 phút để làm thủ tục trước khi khám.\n\n" +
                        "Trân trọng,\nPhòng khám ABC",
                patient.getLastName(),
                patient.getFirstName(),
                doctor.getLastName(),
                doctor.getFirstName(),
                formattedDate,
                appointment.getTimeSlot().getSlotNumber(),
                appointment.getNote() == null || appointment.getNote().isEmpty() ? "Không có" : appointment.getNote()
        );

        email.setBody(body);
        emailService.sendEmail(email);
    }

    private void sendAppointmentEmailToDoctor(User patient, User doctor, Appointment appointment) {
        EmailDTO email = new EmailDTO();
        String formattedDate = DateUtils.formatVietnameseDate(appointment.getAppointmentDate());

        email.setToEmail(doctor.getEmail());
        email.setSubject("Lịch hẹn mới từ bệnh nhân " + patient.getFirstName() + " " + patient.getLastName());

        String body = String.format(
                "Kính gửi Bác sĩ %s %s,\n\n" +
                        "Một bệnh nhân mới đã đặt lịch hẹn khám với bác sĩ.\n\n" +
                        "👤 Tên bệnh nhân: %s %s\n" +
                        "🗓 Ngày khám: %s\n" +
                        "⏰ Ca khám: %s\n" +
                        "📝 Ghi chú: %s\n\n" +
                        "Vui lòng kiểm tra lịch làm việc của mình để chuẩn bị trước buổi khám.\n\n" +
                        "Trân trọng,\nHệ thống quản lý lịch hẹn",
                doctor.getLastName(),
                doctor.getFirstName(),
                patient.getLastName(),
                patient.getFirstName(),
                formattedDate,
                appointment.getTimeSlot().getSlotNumber(),
                appointment.getNote() == null || appointment.getNote().isEmpty() ? "Không có" : appointment.getNote()
        );

        email.setBody(body);
        emailService.sendEmail(email);
    }


    @Override
    public List<AppointmentDTO> getAppointments(String username, String status) {
        // Lấy thông tin người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại!"));

        // Lấy danh sách lịch hẹn theo vai trò
        List<Appointment> appointments;

        if (user.getRole() == UserRole.PATIENT) {
            // Nếu là bệnh nhân, lấy danh sách các lịch hẹn của bệnh nhân
            appointments = appointmentRepository.findByPatientId(user.getId(), status);
        } else if (user.getRole() == UserRole.DOCTOR) {
            // Nếu là bác sĩ, lấy danh sách các lịch hẹn của bác sĩ
            appointments = appointmentRepository.findByDoctorId(user.getId(), status);
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

    @Transactional
    @Override
    public void cancelAppointment(Long appointmentId, String username)
            throws AccessDeniedException, NoSuchElementException, IllegalStateException {

        // Lấy thông tin lịch hẹn
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy lịch hẹn!"));

        // Kiểm tra quyền - chỉ cho phép bệnh nhân sở hữu hoặc bác sĩ được huỷ
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng!"));

        boolean isOwner = appointment.getPatientId().getId().equals(currentUser.getId());
        boolean isDoctor = currentUser.getRole() == UserRole.DOCTOR
                && appointment.getDoctorId().getId().equals(currentUser.getId());

        if (!isOwner && !isDoctor) {
            throw new AccessDeniedException("Bạn không có quyền huỷ lịch hẹn này!");
        }

        // Kiểm tra thời gian - chỉ được huỷ trước 24h
        // Chuyển Date sang LocalDate
        LocalDate appointmentDate = appointment.getAppointmentDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Chuyển String time sang LocalTime
        LocalTime appointmentTime = LocalTime.parse(
                appointment.getTimeSlot().getStartTime(),
                DateTimeFormatter.ofPattern("HH:mm"));

        // Kết hợp thành LocalDateTime
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

        if (LocalDateTime.now().plusHours(24).isAfter(appointmentDateTime)) {
            throw new IllegalStateException(
                    "Không thể huỷ lịch hẹn trong vòng 24 giờ trước giờ khám!");
        }

        // Cập nhật trạng thái
        appointment.setStatus("cancelled");
        appointmentRepository.update(appointment);
    }
}