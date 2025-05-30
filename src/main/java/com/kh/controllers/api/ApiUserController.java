package com.kh.controllers.api;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

import com.kh.dtos.DoctorLicenseDTO;
import com.kh.dtos.DoctorProfileDTO;
import com.kh.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dtos.UserLoginDTO;
import com.kh.dtos.UserDTO;
import com.kh.services.UserService;
import com.kh.utils.ValidationUtils;

@RestController
@RequestMapping("/api")
public class ApiUserController {


// XỬ LÝ YÊU CẦU CỦA NGƯỜI DÙNG
    // NGƯỜI DÙNG MUỐN -> CONTROLLER XỬ LÝ

    @Autowired
    private UserService userService;

    @Autowired
    private ValidationUtils validationUtils;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Endpoint: {@code POST /api/login/}
     * <p>
     * Api xác thực thông tin người dùng
     * </p>
     *
     * @param userDTO Thông tin người dùng cần được xác thực
     * @return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userDTO) {

        // VALIDATE DỮ LIỆU
        ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(userDTO);
        if (errorResponse != null) {
            return errorResponse;
        }

        // TIỀN HÀNH XÁC THỰC NGƯỜI DÙNG VÀ TẠO TOKEN
        try {
            userService.authenticate(userDTO.getUsername(), userDTO.getPassword());
            String token = securityUtils.generateToken(userDTO.getUsername());
            // Trả về JWT token cho người dùng
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } // XỬ LÝ CÁC NGOẠI LỆ
        catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * Endpoint: {@code POST /api/patient-register/}
     *
     * <p>
     * Dùng để đăng ký người dùng loại bệnh nhân. Các trường cần đăng ký được
     * định nghĩa tại {@link com.kh.dtos.UserDTO}
     * </p>
     *
     * @param patientDataMap Phần form-data của bệnh nhân, lưu trữ các thông tin
     *                       cá nhân
     * @param avatarUpload   Ảnh avatar của bệnh nhân upload
     * @return Reponse JSON đối tượng user mới tạo
     */
    @PostMapping(value = "/patient-register", consumes = "multipart/form-data")
    public ResponseEntity<?> patientRegister(
            @RequestParam Map<String, String> patientDataMap,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarUpload
    ) {
        try {
            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // TẠO DTO
            UserDTO patientDTO = new UserDTO();
            patientDTO.setUsername(patientDataMap.get("username"));
            patientDTO.setPassword(patientDataMap.get("password"));
            patientDTO.setConfirmPassword(patientDataMap.get("confirmPassword"));
            patientDTO.setEmail(patientDataMap.get("email"));
            patientDTO.setPhone(patientDataMap.get("phone"));
            patientDTO.setFirstName(patientDataMap.get("firstName"));
            patientDTO.setLastName(patientDataMap.get("lastName"));
            patientDTO.setGender(patientDataMap.get("gender"));
            patientDTO.setAddress(patientDataMap.getOrDefault("address", null));
            patientDTO.setAvatarUpload(avatarUpload);
            patientDTO.setBirthDate(dateFormat.parse(patientDataMap.get("birthDate")));

            // SỬ DỤNG VALIDATOR ĐỂ KIỂM TRA DTO
            ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(patientDTO);
            if (errorResponse != null) {
                return errorResponse;
            }

            // TIẾN HÀNH TẠO ĐỐI TƯỢNG
            UserDTO dto_response = userService.addPatientUser(patientDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto_response);

        } catch (ParseException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("birthDate", "Định dạng ngày gi không hợp lệ!"));
        }
    }

    /**
     * Endpoint: {@code POST /api/doctor-register/}
     *
     * <p>
     * Dùng để đăng ký người dùng loại bệnh nhân. Các trường cần đăng ký được định
     * nghĩa tại {@link com.kh.dtos.DoctorProfileDTO}
     * </p>
     *
     * @param doctorDataMap Phần form-data của bác sĩ, lưu trữ các thông tin
     *                      cá nhân và bằng lưuu hành nghề
     * @param hospitalId    Id của bệnh viện mà bác sĩ khám
     * @param avatarUpload  Ảnh avatar của bác sĩ upload
     * @return Reponse JSON đối tượng user mới tạo
     */
    @PostMapping("/doctor-register")
    public ResponseEntity<?> registerDoctor(
            @RequestParam Map<String, String> doctorDataMap,
            @RequestParam("hospitalId") Long hospitalId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarUpload
    ) {
        try {
            // Parse ngày giờ từ string sang Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // TẠO DTO
            UserDTO doctorDTO = new UserDTO();
            doctorDTO.setUsername(doctorDataMap.get("username"));
            doctorDTO.setPassword(doctorDataMap.get("password"));
            doctorDTO.setConfirmPassword(doctorDataMap.get("confirmPassword"));
            doctorDTO.setEmail(doctorDataMap.get("email"));
            doctorDTO.setPhone(doctorDataMap.get("phone"));
            doctorDTO.setFirstName(doctorDataMap.get("firstName"));
            doctorDTO.setLastName(doctorDataMap.get("lastName"));
            doctorDTO.setGender(doctorDataMap.get("gender"));
            doctorDTO.setAddress(doctorDataMap.getOrDefault("address", null));
            doctorDTO.setAvatarUpload(avatarUpload);
            doctorDTO.setBirthDate(dateFormat.parse(doctorDataMap.get("birthDate")));

            DoctorLicenseDTO doctorLicenseDTO = new DoctorLicenseDTO();
            doctorLicenseDTO.setLicenseNumber(doctorDataMap.get("licenseNumber"));
            doctorLicenseDTO.setSpecialtyId(Long.parseLong(doctorDataMap.get("specialtyId")));
            doctorLicenseDTO.setIssued(dateFormat.parse(doctorDataMap.get("issuedDate")));
            doctorLicenseDTO.setExpiry(dateFormat.parse(doctorDataMap.get("expiryDate")));

            // SỬ DỤNG VALIDATOR ĐỂ KIỂM TRA DTO
            ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(doctorDTO);
            if (errorResponse != null) {
                return errorResponse;
            }

            // TIẾN HÀNH TẠO ĐỐI TƯỢNG
            DoctorProfileDTO dto_response = userService.addDoctorUser(doctorDTO, doctorLicenseDTO, hospitalId);

            return ResponseEntity.status(HttpStatus.CREATED).body(dto_response);

        } catch (ParseException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("birthDate", "Định dạng ngày không hợp lệ!"));
        }
    }

    /**
     * ENDPOINT: {@code /api/secure/profile}
     * <p>
     * Lấy thông tin cá nhân của người dùng.
     * </p>
     *
     * @param principal dữ liệu của người dùng đã được xác thực
     * @return dữ liệu thông tin cá nhân của người dùng.
     */
    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<?> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }
}
