package com.kh.dtos;

import com.kh.pojo.User;

public class DoctorWithRating {
    private User doctor;
    private Double avgRating;

    public DoctorWithRating(User doctor, Double avgRating) {
        this.doctor = doctor;
        this.avgRating = avgRating;
    }

    public User getDoctor() {
        return doctor;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
