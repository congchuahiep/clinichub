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
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import com.kh.services.EmailService;
import com.kh.utils.DateUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    @Autowired
    private Executor emailExecutor;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Transactional
    @Override
    public List<AppointmentSlot> findTakenSlots(Long patientId, Long doctorId, Date date) {
        List<AppointmentSlot> doctorTakenSlots = this.appointmentRepository.findDoctorTakenSlots(doctorId, date);
        List<AppointmentSlot> patientTakenSlots = this.appointmentRepository.findPatientTakenSlots(patientId, date);

        doctorTakenSlots.addAll(patientTakenSlots);

        return doctorTakenSlots.stream()
                .distinct()
                .collect(Collectors.toList());
    }

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
            throw new IllegalArgumentException("Bác sĩ đã có lịch khám vào ca này!");
        }

        // Kiểm tra lịch của bệnh nhân
        if (appointmentRepository.isPatientTimeSlotTaken(
                patient,
                appointmentDTO.getAppointmentDate(),
                timeSlot
        )) {
            throw new IllegalArgumentException("Bạn đã có lịch khám vào ca này!");
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

        // Gửi email bất đồng bộ và không chờ đợi
        CompletableFuture.runAsync(() -> {
            try {
                sendAppointmentEmailToPatient(patient, doctor, savedAppointment);
                sendAppointmentEmailToDoctor(patient, doctor, savedAppointment);
            } catch (Exception e) {
                // Log lỗi nhưng không throw exception
                logger.error("Không thể gửi mail", e);
            }
        }, emailExecutor);


        // Chuyển entity thành DTO để trả về
        return new AppointmentDTO(savedAppointment);
    }

    @Async("emailExecutor")
    protected void sendAppointmentEmailToPatient(User patient, User doctor, Appointment appointment) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async("emailExecutor")
    protected void sendAppointmentEmailToDoctor(User patient, User doctor, Appointment appointment) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Transactional
    @Override
    public AppointmentDTO rescheduleAppointment(Long appointmentId, Date newDate, int newTimeSlot, String username) throws AccessDeniedException {
        // Lấy thông tin lịch hẹn cũ
        Appointment oldAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy lịch hẹn với ID: " + appointmentId));

        // Kiểm tra quyền sở hữu lịch hẹn
        if (!oldAppointment.getPatientId().getUsername().equals(username)) {
            throw new AccessDeniedException("Bạn không có quyền đổi lịch này.");
        }

        // Kiểm tra lịch của bác sĩ
        if (appointmentRepository.isDoctorTimeSlotTaken(
                oldAppointment.getDoctorId(),
                newDate,
                AppointmentSlot.fromSlotNumber(newTimeSlot)
        )) {
            throw new IllegalArgumentException("Bác sĩ đã có lịch khám vào ca này!");
        }

        // Kiểm tra lịch của bệnh nhân
        if (appointmentRepository.isPatientTimeSlotTaken(
                oldAppointment.getPatientId(),
                newDate,
                AppointmentSlot.fromSlotNumber(newTimeSlot)
        )) {
            throw new IllegalArgumentException("Bạn đã có lịch khám vào ca này!");
        }

        // Kiểm tra thời gian - chỉ được huỷ trước 24h
        // Chuyển Date sang LocalDate
        LocalDate appointmentDate = oldAppointment.getAppointmentDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Chuyển String time sang LocalTime
        LocalTime appointmentTime = LocalTime.parse(
                oldAppointment.getTimeSlot().getStartTime(),
                DateTimeFormatter.ofPattern("HH:mm"));

        // Kết hợp thành LocalDateTime
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

        if (LocalDateTime.now().plusHours(24).isAfter(appointmentDateTime)) {
            throw new IllegalStateException(
                    "Không thể đổi lịch hẹn trong vòng 24 giờ trước giờ khám!");
        }

        // Đổi trạng thái lịch hẹn cũ thành "rescheduled"
        oldAppointment.setStatus("rescheduled");
        appointmentRepository.update(oldAppointment);

        // Tạo lịch hẹn mới từ thông tin cũ
        Appointment newAppointment = new Appointment();
        newAppointment.setDoctorId(oldAppointment.getDoctorId());
        newAppointment.setPatientId(oldAppointment.getPatientId());
        newAppointment.setAppointmentDate(newDate);
        newAppointment.setTimeSlot(AppointmentSlot.fromSlotNumber(newTimeSlot));
        newAppointment.setStatus("scheduled");
        newAppointment.setNote(oldAppointment.getNote());

        // Lưu lịch hẹn mới
        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        // Gửi email thông báo cho bệnh nhân và bác sĩ (bất đồng bộ)
        CompletableFuture.runAsync(() -> {
            try {
                sendAppointmentEmailToPatient(savedAppointment.getPatientId(), savedAppointment.getDoctorId(), savedAppointment);
                sendAppointmentEmailToDoctor(savedAppointment.getPatientId(), savedAppointment.getDoctorId(), savedAppointment);
            } catch (Exception e) {
                logger.error("Error sending reschedule emails", e);
            }
        }, emailExecutor);

        return new AppointmentDTO(savedAppointment);
    }
}