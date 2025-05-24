package com.kh.services;

import com.kh.utils.PaginatedResult;
import com.kh.dtos.ReviewDTO;

import java.util.Map;

public interface ReviewService {

    ReviewDTO ratingDoctor(ReviewDTO reviewDTO);

    PaginatedResult<ReviewDTO> getDoctorReviews(Long doctorId, Map<String, String> params);

    Double getDoctorAvgRating(Long doctorId);

    ReviewDTO doctorResponse(Long doctorId, Long reviewId, String doctorResponse);
}
