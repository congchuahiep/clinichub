package com.kh.controllers.api;

import com.kh.dtos.MedicalRecordDTO;
import com.kh.dtos.PaginatedResponseDTO;
import com.kh.enums.UserRole;
import com.kh.services.MedicalRecordService;
import com.kh.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiMedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Endpoint: {@code /api/secure/medical-records}
     *
     * <p>
     * Bệnh nhân được xem danh sách các lần mà bệnh nhân đã chẩn đoán
     * </p>
     */
    @GetMapping("/secure/medical-records")
    public ResponseEntity<?> getMedicalRecords(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication auth
    ) {
        securityUtils.requireRole(auth, UserRole.PATIENT);
        Long patientId = securityUtils.getCurrentUserId(auth);

        PaginatedResponseDTO<MedicalRecordDTO> response = medicalRecordService.getMedicalRecords(patientId, page, size);

        return ResponseEntity.ok(response);
    }
}
