package com.kh.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kh.dtos.PatientRegisterDTO;

public interface UserService extends UserDetailsService {
    boolean authenticate(String username, String password);

    PatientRegisterDTO addPatientUser(PatientRegisterDTO dto);
}
