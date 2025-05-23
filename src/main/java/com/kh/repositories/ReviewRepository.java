package com.kh.repositories;

import com.kh.pojo.Review;

import java.util.List;

public interface ReviewRepository {
    Review save(Review review);

    boolean existsReviewByDoctorAndPatient(Long doctorId, Long patientId);

    List<Review> doctorReviewList(Long doctorId, int page, int pageSize);

    Long countDoctorReview(Long doctorId);
}
