package com.kh.repositories;

import com.kh.pojo.Review;
import com.kh.utils.PaginatedResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewRepository extends GenericRepository<Review, Long> {

    boolean existsReviewByDoctorAndPatient(Long doctorId, Long patientId);

    PaginatedResult<Review> doctorReviewList(Long doctorId, Map<String, String> params);

    Long countDoctorReview(Long doctorId);

    Double calculateAverageRatingByDoctor(Long doctorId);
}
