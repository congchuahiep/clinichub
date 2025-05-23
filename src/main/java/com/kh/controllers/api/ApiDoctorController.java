package com.kh.controllers.api;

import com.kh.dtos.DoctorProfileDTO;
import com.kh.dtos.PaginatedResponseDTO;
import com.kh.dtos.ReviewDTO;
import com.kh.enums.UserRole;
import com.kh.services.ReviewService;
import com.kh.services.UserService;
import com.kh.utils.SecurityUtils;
import com.kh.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
class ApiDoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ValidationUtils validationUtils;

    /**
     * Endpoint: {@code GET /api/doctors}
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

    /**
     * Endpoint: {@code GET /api/doctors/{id}}
     *
     * <p>
     * Xem thông tin chi tiết của một bác sĩ. Bao gồm cả thông tin về đánh giá bác sĩ
     * </p>
     */
    @GetMapping("/doctors/{id}")
    public ResponseEntity<?> retrieveDoctor(@PathVariable Long id) {
        // TODO
        return null;
    }

    /**
     * Endpoint: {@code POST /api/doctors/{id}/reviews}
     *
     * <p>
     * Cho phép bệnh nhân (đã khám bác sĩ này) được đánh giá bác sĩ
     * </p>
     */
    @PostMapping("/secure/doctors/{id}/reviews")
    public ResponseEntity<?> ratingDoctor(
            @PathVariable("id") Long doctorId,
            @RequestBody ReviewDTO reviewDTO,
            Authentication auth
    ) {
        securityUtils.requireRole(auth, UserRole.PATIENT);
        Long patientId = securityUtils.getCurrentUserId(auth);

        reviewDTO.setDoctorId(doctorId);
        reviewDTO.setPatientId(patientId);

        ResponseEntity<?> errorResponse = validationUtils.getValidationErrorResponse(reviewDTO);
        if (errorResponse != null) return errorResponse;

        ReviewDTO dto = reviewService.ratingDoctor(reviewDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Endpoint: {@code GET /api/doctors/{id}/reviews}
     *
     * <p>
     * Xem danh sách các đánh giá của một bác sĩ
     * </p>
     */
    @GetMapping("/doctors/{id}/reviews")
    public ResponseEntity<?> getDoctorReviews(
            @PathVariable("id") Long doctorId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PaginatedResponseDTO<ReviewDTO> reviews = reviewService.getDoctorReviews(doctorId, page, size);

        return ResponseEntity.ok(reviews);
    }
}
