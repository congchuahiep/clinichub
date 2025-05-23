package com.kh.repositories;

import com.kh.pojo.Hospital;
import com.kh.pojo.User;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends GenericRepository<Hospital, Long> {

    void registerDoctorToHospital(Hospital hospital, User doctor);
}
