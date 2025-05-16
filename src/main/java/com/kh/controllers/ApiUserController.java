package com.kh.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dtos.PatientRegisterDTO;
import com.kh.pojo.User;
import com.kh.services.UserService;
import com.kh.utils.JwtUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api")
public class ApiUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    /**
     * Api xác thực thông tin người dùng
     * 
     * <p>
     * 
     * Endpoint: {@code POST /api/login/}
     * 
     * @param user - Thông tin người dùng cần được xác thực
     * @return - JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean authenticated = userService.authenticate(user.getUsername(), user.getPassword());
        if (!authenticated) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Sai thông tin đăng nhập");
        }

        try {
            String token = JwtUtils.generateToken(user.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo JWT");
        }
    }

    /**
     * Endpoint: {@code GET /api/patient-register/}
     * 
     * <p>
     * Dùng để đăng ký người dùng loại bệnh nhân. Các trường cần đăng ký được định
     * nghĩa tại {@link com.kh.dtos.PatientRegisterDTO}
     * 
     * @param patientDataMap - Phần form-data của bệnh nhân, lưu trữ các thông tin
     *                       cá nhân
     * @param avatarUpload   - Ảnh avatar của bệnh nhân upload
     * @return Reponse JSON đối tượng user mới tạo
     * @exception tênTrường Trường dữ liệu validate không thành công
     * @exception error     Các thông báo lỗi khác
     */
    @PostMapping(value = "/patient-register", consumes = "multipart/form-data")
    public ResponseEntity<?> patientRegister(
            @RequestParam Map<String, String> patientDataMap,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarUpload) {

        // TẠO DTO
        PatientRegisterDTO dto = new PatientRegisterDTO();
        dto.setUsername(patientDataMap.get("username"));
        dto.setPassword(patientDataMap.get("password"));
        dto.setEmail(patientDataMap.get("email"));
        dto.setPhone(patientDataMap.get("phone"));
        dto.setFirstName(patientDataMap.get("firstName"));
        dto.setLastName(patientDataMap.get("lastName"));
        dto.setGender(patientDataMap.get("gender"));
        dto.setAddress(patientDataMap.getOrDefault("address", null));
        dto.setAvatarUpload(avatarUpload);

        try {
            if (patientDataMap.get("birthDate") != null && !patientDataMap.get("birthDate").isEmpty()) {
                dto.setBirthDate(java.sql.Date.valueOf(patientDataMap.get("birthDate")));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("birthDate", "Ngày sinh không đúng!"));
        }

        // Sử dụng Validator để kiểm tra DTO
        Set<ConstraintViolation<PatientRegisterDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<PatientRegisterDTO> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        // TIẾN HÀNH TẠO ĐỐI TƯỢNG
        try {
            PatientRegisterDTO dto_response = userService.addPatientUser(dto);
            return ResponseEntity.ok(dto_response);
        }

        // Bắt ngoại lệ trùng trường username hoặc email
        catch (org.hibernate.exception.ConstraintViolationException ex) {
            // Kiểm tra thông điệp lỗi để xác định trường nào bị trùng
            String message = ex.getMessage();
            if (message.contains("users.username")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("username", "Tên đăng nhập đã tồn tại!"));
            } else if (message.contains("users.email")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("email", "Email đã tồn tại!"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Có phần dữ liệu bị trùng lặp!"));
        }
        // Bắt ngoại lệ khác
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getClass().getCanonicalName() + e.getMessage()));
        }
    }

}
