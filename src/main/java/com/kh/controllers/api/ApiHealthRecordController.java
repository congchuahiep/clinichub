package com.kh.controllers.api;

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
     * GET /api/secure/health-records
     * Lấy hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     */
//    @GetMapping
//    public ResponseEntity<?> getMyHealthRecord(Authentication auth) {
//        try {
//            String username = auth.getName();
//            User user = userService.(username);
//
//
//
//            return ResponseEntity.ok(dto);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Lỗi khi lấy hồ sơ sức khỏe"));
//        }
//    }

    /**
     * PUT /api/secure/health-records
     * Cập nhật hồ sơ sức khỏe của chính bệnh nhân đang đăng nhập
     */
//    @PutMapping
//    public ResponseEntity<?> updateMyHealthRecord(@RequestBody PatientProfileDTO dto, Principal principal) {
//        try {
//            String username = principal.getName();
////            User user = userService.getUserByUsernameFull(username);
//
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Collections.singletonMap("error", "Người dùng không tồn tại"));
//            }
//
//            if (user.getRole() != UserRole.PATIENT) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Collections.singletonMap("error", "Chỉ bệnh nhân mới được phép cập nhật hồ sơ sức khỏe"));
//            }
//
//
//            PatientProfileDTO updatedDto = healthRecordService.updateHealthRecord(user.getId(), dto);
//            return ResponseEntity.ok(updatedDto);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Lỗi khi cập nhật hồ sơ sức khỏe"));
//        }
//    }

    @GetMapping("/secure/health-records/{id}")
    public ResponseEntity<?> doctorGetPatientProfile(@PathVariable("id") Long patientId, Authentication auth) {
        PatientProfileDTO dto = this.healthRecordService.doctorGetHealthRecordByPatientId(patientId, auth.getName());

        return ResponseEntity.ok(dto);
    }
}
