package com.kh.controllers.api;

import com.kh.dtos.DoctorProfileDTO;
import com.kh.dtos.PaginatedResponseDTO;
import com.kh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class ApiDoctorController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint: {@code /api/doctors}
     *
     * <p>
     * Cho phép tìm kiếm bác sĩ, tìm kiếm theo bệnh viện, tìm kiếm theo chuyên khoa, tìm kiếm theo tên bác sĩ
     * </p>
     */
    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "hospitalId", required = false) Long hospitalId,
            @RequestParam(name = "specialtyId", required = false) Long specialtyId,
            @RequestParam(name = "doctorName", required = false) String doctorName
    ) {

        PaginatedResponseDTO<DoctorProfileDTO> response = userService.getDoctors(
                page,
                size,
                hospitalId,
                specialtyId,
                doctorName
        );

        return ResponseEntity.ok(response);
    }
}
