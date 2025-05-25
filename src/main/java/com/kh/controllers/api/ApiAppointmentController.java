package com.kh.controllers.api;

import com.kh.dtos.AppointmentDTO;
import com.kh.dtos.MedicalRecordDTO;
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
     *
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

}