package com.kh.repositories;

import com.kh.pojo.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Review update(Review review);

    Optional<Review> findById(Long reviewId);

    boolean existsReviewByDoctorAndPatient(Long doctorId, Long patientId);

    List<Review> doctorReviewList(Long doctorId, int page, int pageSize);

    Long countDoctorReview(Long doctorId);
}
