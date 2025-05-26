package com.kh.controllers.api;

import com.kh.dtos.HealthRecordDTO;
import com.kh.dtos.MedicalRecordDTO;
import com.kh.utils.PaginatedResult;
import com.kh.dtos.PatientProfileDTO;
import com.kh.enums.UserRole;
import com.kh.services.HealthRecordService;
import com.kh.services.MedicalRecordService;
import com.kh.services.UserService;

import com.kh.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiHealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * Endpoints: {@code GET /api/secure/health-records}
     * <p>
     * Lấy hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     * </p>
     */
    @GetMapping("/secure/health-records")
    public ResponseEntity<?> getHealthRecord(Authentication auth) {
        // KIỂM TRA QUYỀN
        securityUtils.requireRole(auth, UserRole.PATIENT);

        // Tiến hành truy vấn dữ liệu
        HealthRecordDTO dto = this.healthRecordService.getHealthRecord(auth.getName());

        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoints: {@code PUT /api/secure/health-records}
     * <p>
     * Cập nhật hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     * </p>
     */
    @PutMapping("/secure/health-records")
    public ResponseEntity<?> putHealthRecord(
            @RequestBody HealthRecordDTO healthRecordDTO,
            Authentication auth
    ) {
        // KIỂM TRA QUYỀN
        securityUtils.requireRole(auth, UserRole.PATIENT);

        // Tiến hành cập nhật dữ liệu
        HealthRecordDTO dto = this.healthRecordService.putHealthRecord(auth.getName(), healthRecordDTO);

        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoints: {@code GET /api/secure/health-records/{id}}
     *
     * <p>
     * Truy cập và xem chi tiết một hồ sơ sức khoẻ của bệnh nhân mà bác sĩ đã/đang khám
     * </p>
     *
     * @param patientId Id bệnh nhân cần xem hồ sơ
     * @param auth      Thông tin người dùng
     */
    @GetMapping("/secure/health-records/{id}")
    public ResponseEntity<?> doctorGetPatientProfile(@PathVariable("id") Long patientId, Authentication auth) {
        PatientProfileDTO dto = this.healthRecordService.getPatientProfile(patientId, auth.getName());

        return ResponseEntity.ok(dto);
    }

    /**
     * Endpoints: {@code PUT /api/secure/health-records/{id}}
     *
     * <p>
     * Chỉnh sửa một hồ sơ sức khoẻ của bệnh nhân mà bác sĩ đã/đang khám
     * </p>
     *
     * @param patientId Id bệnh nhân
     * @param auth      Thông tin người dùng
     */
    @PutMapping("/secure/health-records/{id}")
    public ResponseEntity<?> doctorPutPatientProfile(
            @PathVariable("id") Long patientId,
            @RequestBody HealthRecordDTO healthRecordDTO,
            Authentication auth
    ) {
        HealthRecordDTO dto = this.healthRecordService.updateHealthRecord(patientId, auth.getName(), healthRecordDTO);

        return ResponseEntity.ok(dto);
    }

    /**
     * Bác sĩ xem danh sách các bản ghi chẩn đoán của một hồ sơ sức khoẻ (của 1 bệnh nhân)
     */
    @GetMapping("/secure/health-records/{id}/medical-records")
    public ResponseEntity<?> doctorGetMedicalRecords(
            @PathVariable("id") Long patientId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication auth
    ) {
        securityUtils.requireRole(auth, UserRole.DOCTOR);
        Long doctorId = securityUtils.getCurrentUserId(auth);

        PaginatedResult<MedicalRecordDTO> dtos = medicalRecordService.doctorGetMedicalRecords(doctorId, patientId, page, size);

        return ResponseEntity.ok(dtos);
    }
}
