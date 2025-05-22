package com.kh.repositories;

import com.kh.pojo.Hospital;
import com.kh.pojo.User;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository {
    Optional<Hospital> findById(Long id);

    List<Hospital> list();

    void registerDoctorToHospital(Hospital hospital, User doctor);
}
