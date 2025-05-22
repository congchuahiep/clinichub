package com.kh.dtos;

import com.kh.pojo.HealthRecord;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

public class HealthRecordDTO {

    private Long id;

    @Size(max = 65535)
    private String medicalHistory;

    @Size(max = 65535)
    private String allergies;

    @Size(max = 65535)
    private String chronicConditions;

    private Float weight;

    private Float height;

    private String bloodPressure;

    private String bloodSugar;

    private Set<MedicalRecordDTO> medicalRecords;

    public HealthRecordDTO() {
    }

    public HealthRecordDTO(HealthRecord healthRecord) {
        this.id = healthRecord.getPatient().getId();
        this.medicalHistory = healthRecord.getMedicalHistory();
        this.allergies = healthRecord.getAllergies();
        this.chronicConditions = healthRecord.getChronicConditions();
        this.weight = healthRecord.getWeight();
        this.height = healthRecord.getHeight();
        this.bloodPressure = healthRecord.getBloodPressure();
        this.bloodSugar = healthRecord.getBloodSugar();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<MedicalRecordDTO> getMedicalRecords() {
        return medicalRecords;
    }
    public void setMedicalRecords(Set<MedicalRecordDTO> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }
}
