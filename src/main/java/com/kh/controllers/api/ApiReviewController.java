package com.kh.controllers.api;

import com.kh.dtos.ReviewDTO;
import com.kh.enums.UserRole;
import com.kh.services.ReviewService;
import com.kh.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
class ApiReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/secure/reviews/{id}/response")
    public ResponseEntity<?> doctorResponse(
            @PathVariable("id") Long reviewId,
            @RequestParam("doctorResponse") String doctorResponse,
            Authentication auth
    ) {
        securityUtils.requireRole(auth, UserRole.DOCTOR);
        Long doctorId = securityUtils.getCurrentUserId(auth);

        ReviewDTO reviewDTO = reviewService.doctorResponse(doctorId, reviewId, doctorResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
    }
}
