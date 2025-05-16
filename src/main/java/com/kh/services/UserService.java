package com.kh.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kh.dtos.UserDTO;

public interface UserService extends UserDetailsService {
    boolean authenticate(String username, String password);

    UserDTO addPatientUser(UserDTO patientDTO);

    UserDTO getUserByUsername(String username);
}
