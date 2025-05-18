package com.kh.controllers.api;

import com.kh.dtos.AppointmentDTO;
import com.kh.services.AppointmentService;
import com.kh.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

@Controller
@RequestMapping("/api")
public class ApiAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ValidationUtils validationUtils;

    @PostMapping("/secure/appointments")
    public ResponseEntity<?> addAppointment(
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("appointmentDatetime") String appointmentDatetime,
            @RequestParam(value = "note", required = false) String note,
            Principal principal) {

        try {
            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Tạo DTO từ form data
            AppointmentDTO appointmentDTO = new AppointmentDTO();
            appointmentDTO.setAppointmentDatetime(dateFormat.parse(appointmentDatetime));
            appointmentDTO.setDoctorId(doctorId);
            appointmentDTO.setNote(note);

            // VALIDATE DỮ LIỆU
            ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(appointmentDTO);
            if (errorResponse != null)
                return errorResponse;

            // Gọi service để tạo lịch hẹn
            AppointmentDTO createdAppointment = appointmentService.addAppointment(
                    appointmentDTO, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);

        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("appointmentDatetime", "Định dạng ngày giờ không hợp lệ!"));
        }
    }


}
