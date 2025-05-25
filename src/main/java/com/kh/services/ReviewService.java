package com.kh.services;

import com.kh.enums.ReviewCheckResult;
import com.kh.utils.PaginatedResult;
import com.kh.dtos.ReviewDTO;

import java.util.Map;

public interface ReviewService {

    ReviewDTO ratingDoctor(ReviewDTO reviewDTO);

    PaginatedResult<ReviewDTO> getDoctorReviews(Long doctorId, Map<String, String> params);

    Double getDoctorAvgRating(Long doctorId);

    ReviewCheckResult checkPatientReview(Long doctorId, Long patientId);

    ReviewDTO doctorResponse(Long doctorId, Long reviewId, String doctorResponse);
}
