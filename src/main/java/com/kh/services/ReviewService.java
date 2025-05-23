package com.kh.services;

import com.kh.dtos.PaginatedResponseDTO;
import com.kh.dtos.ReviewDTO;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {

    ReviewDTO ratingDoctor(ReviewDTO reviewDTO);

    @Transactional
    PaginatedResponseDTO<ReviewDTO> getDoctorReviews(Long doctorId, int page, int pageSize);
}
