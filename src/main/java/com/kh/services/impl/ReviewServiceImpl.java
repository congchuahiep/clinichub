package com.kh.services.impl;

import com.kh.dtos.PaginatedResponseDTO;
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

import java.util.List;

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
    public PaginatedResponseDTO<ReviewDTO> getDoctorReviews(Long doctorId, int page, int pageSize) {

        Long totalElements = reviewRepository.countDoctorReview(doctorId);

        List<Review> reviews = reviewRepository.doctorReviewList(doctorId, page, pageSize);

        List<ReviewDTO> reviewDTOS = reviews.stream().map(ReviewDTO::new).toList();

        return new PaginatedResponseDTO<>(
                reviewDTOS, page, pageSize, totalElements,
                (int) Math.ceil(totalElements / (double) pageSize)
        );
    }
}
