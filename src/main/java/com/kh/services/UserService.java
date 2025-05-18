package com.kh.services;

import com.kh.exceptions.FileUploadException;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.kh.dtos.UserDTO;

public interface UserService extends UserDetailsService {
    boolean authenticate(String username, String password);

    UserDTO addPatientUser(UserDTO patientDTO) throws FileUploadException;

    UserDTO getUserByUsername(String username);
}
