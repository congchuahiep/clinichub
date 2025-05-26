package com.kh.controllers.api;

import com.kh.dtos.AppointmentDTO;
import com.kh.dtos.MedicalRecordDTO;
import com.kh.enums.AppointmentSlot;
import com.kh.enums.UserRole;
import com.kh.services.AppointmentService;
import com.kh.services.MedicalRecordService;
import com.kh.utils.SecurityUtils;
import com.kh.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ApiAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Endpoint: {@code /api/secure/appointments}
     *
     * <p>
     * Cho phép người dùng bệnh nhân tạo một lịch khám mới
     * </p>
     */
    @PostMapping("/secure/appointments")
    public ResponseEntity<?> addAppointment(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("appointmentDate") String appointmentDate,
            @RequestParam("timeSlot") int timeSlot,
            @RequestParam(value = "note", required = false) String note,
            Authentication auth
    ) {
        try {
            // KIỂM TRA QUYỀN
            securityUtils.requireRole(auth, UserRole.PATIENT);

            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Tạo DTO từ form data
            AppointmentDTO appointmentDTO = new AppointmentDTO();
            appointmentDTO.setAppointmentDate(dateFormat.parse(appointmentDate));
            appointmentDTO.setDoctorId(doctorId);
            appointmentDTO.setNote(note);
            appointmentDTO.setTimeSlot(timeSlot);

            // VALIDATE DỮ LIỆU
            ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(appointmentDTO);
            if (errorResponse != null)
                return errorResponse;

            // Gọi service để tạo lịch hẹn
            AppointmentDTO createdAppointment = appointmentService.addAppointment(
                    appointmentDTO, auth.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);

        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("appointmentDatetime", "Định dạng ngày giờ không hợp lệ!"));
        }
    }

    /**
     * Endpoint: {@code /api/secure/appointments}
     *
     * <p>
     * Lấy danh sách các lịch khám của bác sĩ và bệnh nhân
     * </p>
     */
    @GetMapping("/secure/appointments")
    public ResponseEntity<?> getAppointments(
            Authentication auth,
            @RequestParam(value = "status", required = false) String status
    ) {
        try {
            // Kiểm tra quyền của người dùng (bác sĩ hoặc bệnh nhân)

            // Lấy danh sách các lịch khám (cả bác sĩ và bệnh nhân)
            List<AppointmentDTO> appointments = appointmentService.getAppointments(auth.getName(), status);

            return ResponseEntity.ok(appointments);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Lỗi khi lấy danh sách lịch khám." + e.getMessage()));
        }
    }

    /**
     * Endpoint: {@code /api/secure/appointments/{id}/medical-records}
     *
     * <p>
     * Sau khi đã khám xong, bác sĩ tạo ra một bản ghi chẩn đoán bệnh mới
     * </p>
     */
    @PostMapping("/secure/appointments/{id}/medical-records")
    public ResponseEntity<?> addMedicalRecord(
            @PathVariable("id") Long appointmentId,
            @RequestBody MedicalRecordDTO medicalRecordDTO,
            Authentication auth
    ) {
        securityUtils.requireRole(auth, UserRole.DOCTOR);

        medicalRecordDTO.setAppointmentId(appointmentId);
        medicalRecordDTO.setDoctorId(securityUtils.getCurrentUserId(auth));

        // SỬ DỤNG VALIDATOR ĐỂ KIỂM TRA DTO
        ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(medicalRecordDTO);
        if (errorResponse != null) {
            return errorResponse;
        }

        medicalRecordDTO = medicalRecordService.addMedicalRecord(medicalRecordDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordDTO);
    }


    /**
     * Endpoint: {@code /api/secure/appointments/{id}}
     * <p>
     * Xem chi tiết lịch khám
     */
    @GetMapping("/secure/appointments/{appointmentId}")
    public ResponseEntity<?> getAppointmentDetails(
            @PathVariable("appointmentId") Long appointmentId,
            Authentication auth
    ) {
        try {
            // Lấy chi tiết lịch khám và bản ghi khám (nếu có)
            AppointmentDTO detailsDTO = appointmentService.getAppointmentDetails(appointmentId, auth.getName());
            return ResponseEntity.ok(detailsDTO);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Endpoint: {@code /api/secure/appointments/{id}/cancel}
     *
     * <p>
     * Cho phép bệnh nhân sở hữu lịch khám hoặc bác sĩ huỷ lịch hẹn.
     * Chỉ được huỷ trước thời điểm hẹn 24 giờ
     * </p>
     */
    @PostMapping("/secure/appointments/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable("id") Long appointmentId,
            Authentication auth
    ) {
        try {
            appointmentService.cancelAppointment(appointmentId, auth.getName());
            return ResponseEntity.ok(Collections.singletonMap("message", "Huỷ lịch hẹn thành công"));

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Không tìm thấy lịch hẹn!"));
        }
    }

    @PostMapping("/secure/appointments/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable("id") Long appointmentId,
            @RequestParam("newAppointmentDate") String newAppointmentDate,
            @RequestParam("newTimeSlot") int newTimeSlot,
            Authentication auth
    ) {
        try {
            // Kiểm tra quyền truy cập và vai trò
            securityUtils.requireRole(auth, UserRole.PATIENT);

            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = dateFormat.parse(newAppointmentDate);

            // Gọi service để đổi lịch hẹn
            AppointmentDTO updatedAppointment = appointmentService.rescheduleAppointment(
                    appointmentId,
                    newDate,
                    newTimeSlot,
                    auth.getName()
            );

            return ResponseEntity.status(HttpStatus.OK).body(updatedAppointment);

        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Định dạng ngày không hợp lệ!"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/secure/appointments/taken-slots")
    public ResponseEntity<?> findTakenSlots(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("date") String date,
            Authentication auth
    ) {
        try {
            securityUtils.requireRole(auth, UserRole.PATIENT);
            Long patientId = securityUtils.getCurrentUserId(auth);

            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            List<AppointmentSlot> takenSlots = appointmentService.findTakenSlots(patientId, doctorId, dateFormat.parse(date));

            return ResponseEntity.ok(takenSlots);

        }  catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("appointmentDatetime", "Định dạng ngày giờ không hợp lệ!"));
        }
    }
}