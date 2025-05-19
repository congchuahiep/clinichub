/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.dtos;

/**
 *
 * @author Admin
 */
public class DoctorProfileDTO {
    private UserDTO doctorDTO;
    private DoctorLicenseDTO doctorLicenseDTO;
    
    public DoctorProfileDTO(UserDTO userDTO, DoctorLicenseDTO doctorLicenseDTO) {
        this.doctorDTO = userDTO;
        this.doctorLicenseDTO = doctorLicenseDTO;
    }

    /**
     * @return the doctorDTO
     */
    public UserDTO getDoctorDTO() {
        return doctorDTO;
    }

    /**
     * @param doctorDTO the doctorDTO to set
     */
    public void setDoctorDTO(UserDTO doctorDTO) {
        this.doctorDTO = doctorDTO;
    }

    /**
     * @return the doctorLicenseDTO
     */
    public DoctorLicenseDTO getDoctorLicenseDTO() {
        return doctorLicenseDTO;
    }

    /**
     * @param doctorLicenseDTO the doctorLicenseDTO to set
     */
    public void setDoctorLicenseDTO(DoctorLicenseDTO doctorLicenseDTO) {
        this.doctorLicenseDTO = doctorLicenseDTO;
    }
    
    
}
