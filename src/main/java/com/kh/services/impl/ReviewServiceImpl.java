package com.kh.services.impl;

import com.kh.enums.ReviewCheckResult;
import com.kh.utils.PaginatedResult;
import com.kh.dtos.ReviewDTO;
import com.kh.dtos.UserDTO;
import com.kh.pojo.Review;
import com.kh.pojo.User;
import com.kh.repositories.AppointmentRepository;
import com.kh.repositories.ReviewRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ReviewDTO ratingDoctor(ReviewDTO reviewDTO) {
        // Kiểm tra người dùng đã khám bác sĩ này chưa
        if (!appointmentRepository.existsAppointmentBetweenDoctorAndPatient(reviewDTO.getDoctorId(), reviewDTO.getPatientId())
        ) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sau khi đã khám bác sĩ này!");
        }

        // Kiểm tra xem bệnh nhân đã đánh giá bác sĩ này chưa
        if (reviewRepository.existsReviewByDoctorAndPatient(reviewDTO.getDoctorId(), reviewDTO.getPatientId())) {
            throw new RuntimeException("Bạn đã đánh giá bác sĩ này rồi!");
        }

        User doctor = userRepository.findDoctorById(reviewDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Bác sĩ này không tồn tại!"));

        User patient = userRepository.findById(reviewDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Người dùng truy cập không hợp lệ!"));

        Review savedReview = reviewRepository.save(new Review(reviewDTO, doctor, patient));

        reviewDTO.setId(savedReview.getId());
        reviewDTO.setPatient(new UserDTO(patient));
        reviewDTO.setCreatedAt(savedReview.getCreatedAt());

        return reviewDTO;
    }

    @Transactional
    @Override
    public PaginatedResult<ReviewDTO> getDoctorReviews(Long doctorId, Map<String, String> params) {
        PaginatedResult<Review> reviews = reviewRepository.doctorReviewList(doctorId, params);
        return reviews.mapTo(ReviewDTO::new);
    }

    @Override
    public ReviewCheckResult checkPatientReview(Long doctorId, Long patientId) {
        // Kiểm tra xem bệnh nhân đã đánh giá bác sĩ này chưa
        if (reviewRepository.existsReviewByDoctorAndPatient(doctorId, patientId)) {
            return ReviewCheckResult.ALREADY_REVIEWED;
        }

        // Kiểm tra xem bệnh nhân đã có lịch hẹn hoàn thành với bác sĩ chưa
        if (!appointmentRepository.existsCompletedAppointmentBetweenDoctorAndPatient(doctorId, patientId)) {
            return ReviewCheckResult.NO_COMPLETED_APPOINTMENT;
        }

        return ReviewCheckResult.ALLOWED;
    }


    @Override
    public ReviewDTO doctorResponse(Long doctorId, Long reviewId, String doctorResponse) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Đánh giá này không tồn tại!"));

        if (!review.getDoctorId().getId().equals(doctorId)) {
            throw new RuntimeException("Bạn không có quyền phản hồi đánh giá này");
        }

        review.setDoctorResponse(doctorResponse);
        review.setDoctorResponseDate(new java.util.Date());

        reviewRepository.update(review);

        return new ReviewDTO(review);
    }

    @Override
    public Double getDoctorAvgRating(Long doctorId) {
        return 0.0;
    }
}
