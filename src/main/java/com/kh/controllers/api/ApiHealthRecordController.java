package com.kh.controllers.api;

import com.kh.dtos.HealthRecordDTO;
import com.kh.dtos.PatientProfileDTO;
import com.kh.services.HealthRecordService;
import com.kh.services.UserService;

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

    /**
     * Endpoints: {@code GET /api/secure/health-records}
     * Lấy hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     */


    /**
     * Endpoints: {@code PUT /api/secure/health-records}
     * Cập nhật hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     */

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
     * Truy cập và xem chi tiết một hồ sơ sức khoẻ của bệnh nhân mà bác sĩ đã/đang khám
     * </p>
     *
     * @param patientId Id bệnh nhân cần xem hồ sơ
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
}
