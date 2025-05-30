package com.kh.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;

public class PatientProfileDTO {

    // Thông tin User

    private UserDTO patient;

    // Thông tin HealthRecord

    private String medicalHistory;

    private String allergies;

    private String chronicConditions;

    private Float weight;

    private Float height;

    private String bloodPressure;

    private String bloodSugar;

    // Constructors

    public PatientProfileDTO() {}

    public PatientProfileDTO(User patient, HealthRecord healthRecord) {
        this.patient = new UserDTO(patient);
        this.medicalHistory = healthRecord.getMedicalHistory();
        this.allergies = healthRecord.getAllergies();
        this.chronicConditions = healthRecord.getChronicConditions();
        this.weight = healthRecord.getWeight();
        this.height = healthRecord.getHeight();
        this.bloodPressure = healthRecord.getBloodPressure();
        this.bloodSugar = healthRecord.getBloodSugar();
    }


    // Bạn có thể tạo thêm constructor nhận User và HealthRecord hoặc builder

    // Getters & Setters

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }
    

}
