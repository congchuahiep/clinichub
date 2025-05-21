 package com.kh.controllers.api;

import com.kh.dtos.AppointmentDTO;
import com.kh.enums.UserRole;
import com.kh.services.AppointmentService;
import com.kh.utils.SecurityUtils;
import com.kh.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

@RestController
@RequestMapping("/api")
public class ApiAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ValidationUtils validationUtils;
    @Autowired
    private SecurityUtils securityUtils;

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
}
