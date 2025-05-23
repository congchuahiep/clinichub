package com.kh.repositories;

import com.kh.pojo.Review;

public interface ReviewRepository {
    Review save(Review review);

    boolean existsReviewByDoctorAndPatient(Long doctorId, Long patientId);
}
