package com.kh.services;

import com.kh.dtos.DoctorLicenseDTO;
import com.kh.dtos.DoctorProfileDTO;
import com.kh.exceptions.FileUploadException;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.kh.dtos.UserDTO;
import com.kh.pojo.User;

public interface UserService extends UserDetailsService {
    void authenticate(String username, String password);

    UserDTO addPatientUser(UserDTO patientDTO) throws FileUploadException;

    UserDTO getUserByUsername(String username);
    
    DoctorProfileDTO addDoctorUser(UserDTO doctorDTO, DoctorLicenseDTO doctorLicense) throws FileUploadException;
    
    User getUserByUsernameFull(String username);
}
