package com.kh.controllers.api;

import com.kh.dtos.PatientProfileDTO;
import com.kh.services.HealthRecordService;

import java.security.Principal;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient-profile")
public class ApiPatientProfileController {

    @Autowired
    private HealthRecordService healthRecordService;

    @GetMapping("/health-records/{patientId}")
    public ResponseEntity<?> getHealthRecord(@PathVariable Long patientId, Principal principal) {
        // Giả sử lấy doctorId từ principal
        Long doctorId = getDoctorIdFromPrincipal(principal);

        PatientProfileDTO dto = healthRecordService.getHealthRecord(doctorId, patientId);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Bạn không có quyền xem hồ sơ bệnh nhân này"));
        }
        return ResponseEntity.ok(dto);
    }

    private Long getDoctorIdFromPrincipal(Principal principal) {
        // TODO: Implement lấy doctorId từ principal (token hoặc username)
        // Ví dụ giả lập
        return 1L;
    }
}
