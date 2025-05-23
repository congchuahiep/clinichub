/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.dtos;

import com.kh.pojo.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Admin
 */
public class DoctorProfileDTO {
    private UserDTO doctorDTO;
    private Set<DoctorLicenseDTO> doctorLicenseDTOSet;
    private Set<HospitalDTO> hospitalDTOSet;

    public DoctorProfileDTO() {

    }

    public DoctorProfileDTO(UserDTO doctorDTO, DoctorLicenseDTO doctorLicenseDTO) {
        this.doctorDTO = doctorDTO;
    }

    public DoctorProfileDTO(User doctor) {
        this.setDoctorDTO(new UserDTO(doctor));
        this.setHospitalDTOSet(doctor.getHospitalSet()
                .stream()
                .map(HospitalDTO::new)
                .collect(Collectors.toSet())
        );
        this.setDoctorLicenseDTOSet(doctor.getDoctorLicenseSet()
                .stream()
                .map(DoctorLicenseDTO::new)
                .collect(Collectors.toSet())
        );
    }
//
//    public DoctorProfileDTO(UserDTO userDTO, Set<DoctorLicenseDTO> doctorLicenseDTOSet, Set<HospitalDTO> hospitalDTOSet) {
//        this.doctorDTO = userDTO;
//        this.doctorLicenseDTOSet = doctorLicenseDTOSet;
//        this.hospitalDTOSet = hospitalDTOSet;
//    }

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
    public Set<DoctorLicenseDTO> getDoctorLicenseDTOSet() {
        return doctorLicenseDTOSet;
    }

    /**
     * @param doctorLicenseDTOSet the doctorLicenseDTO to set
     */
    public void setDoctorLicenseDTOSet(Set<DoctorLicenseDTO> doctorLicenseDTOSet) {
        this.doctorLicenseDTOSet = doctorLicenseDTOSet;
    }

    public Set<HospitalDTO> getHospitalDTOSet() {
        return hospitalDTOSet;
    }

    public void setHospitalDTOSet(Set<HospitalDTO> hospitalDTOSet) {
        this.hospitalDTOSet = hospitalDTOSet;
    }
}
