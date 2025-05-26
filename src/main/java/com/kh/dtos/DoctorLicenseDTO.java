package com.kh.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kh.pojo.DoctorLicense;
import com.kh.pojo.Specialty;
import com.kh.pojo.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
public class DoctorLicenseDTO {
    
    @NotBlank
    @JsonIgnore
    private long doctorId;
    
    @NotBlank
    @Size(max = 100, message = "Vượt mức pickelball")
    private String licenseNumber;
    
    @NotBlank
    @JsonIgnore
    private long specialtyId;
    private String specialtyName;
    
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date issued;
    
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date expiry;

    public DoctorLicenseDTO() {}

    public DoctorLicenseDTO(DoctorLicense doctorLicense) {
        this.doctorId = doctorLicense.getDoctorId().getId();
        this.licenseNumber = doctorLicense.getLicenseNumber();
        this.specialtyId = doctorLicense.getSpecialtyId().getId();
        this.issued = doctorLicense.getIssuedDate();
        this.expiry = doctorLicense.getExpiryDate();
        this.specialtyName = doctorLicense.getSpecialtyId().getName();
    }

    public DoctorLicense toObject(User doctor, Specialty specialty) {
        DoctorLicense doctorLicense = new DoctorLicense();
        doctorLicense.setLicenseNumber(this.licenseNumber);
        doctorLicense.setExpiryDate(this.expiry);
        doctorLicense.setIssuedDate(this.issued);
        doctorLicense.setSpecialtyId(specialty);
        doctorLicense.setDoctorId(doctor);
        doctorLicense.setStatus("pending");
        this.specialtyName = specialty.getName();
        
        return doctorLicense;
    }

    /**
     * @return the doctorId
     */
    public long getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId the doctorId to set
     */
    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * @return the licenseNumber
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * @param licenseNumber the licenseNumber to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * @return the specialtyId
     */
    public long getSpecialtyId() {
        return specialtyId;
    }

    /**
     * @param specialtyId the specialtyId to set
     */
    public void setSpecialtyId(long specialtyId) {
        this.specialtyId = specialtyId;
    }

    /**
     * @return the issued
     */
    public Date getIssued() {
        return issued;
    }

    /**
     * @param issued the issued to set
     */
    public void setIssued(Date issued) {
        this.issued = issued;
    }

    /**
     * @return the expiry
     */
    public Date getExpiry() {
        return expiry;
    }

    /**
     * @param expiry the expiry to set
     */
    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }
}
