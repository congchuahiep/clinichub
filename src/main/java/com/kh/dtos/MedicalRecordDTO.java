package com.kh.dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kh.pojo.MedicalRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicalRecordDTO {

    @NotNull
    private Long appointmentId;           // ID của lịch khám

    private Long healthRecordId;          // ID của hồ sơ sức khỏe

    @NotNull
    private Long doctorId;                // ID của bác sĩ chẩn đoán

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Long diseaseId;              // ID của bệnh (nếu có)
    private String diseaseName;

    @NotBlank
    private String diagnosis;             // Mô tả chẩn đoán

    private Date diagnosisDate;           // Ngày chẩn đoán

    private String prescriptions;         // Đơn thuốc (có thể rỗng)

    private String testResults;           // Kết quả xét nghiệm (có thể rỗng)

    private String notes;                 // Ghi chú của bác sĩ (có thể rỗng)

    @JsonIgnore
    private Date createdAt;               // Ngày tạo bản ghi

    @JsonIgnore
    private Date updatedAt;               // Ngày cập nhật bản ghi

    // Constructor rỗng
    public MedicalRecordDTO() {}

    public MedicalRecordDTO(MedicalRecord medicalRecord) {
        this.appointmentId = medicalRecord.getAppointment().getId();
        this.doctorId = medicalRecord.getDoctorId().getId();
        this.diseaseName = medicalRecord.getDiseaseId() != null ? medicalRecord.getDiseaseId().getName() : null;
        this.diagnosis = medicalRecord.getDiagnosis();
        this.diagnosisDate = medicalRecord.getDiagnosisDate();
        this.healthRecordId = medicalRecord.getHealthRecordId().getPatient().getId();
        this.notes = medicalRecord.getNotes();
        this.prescriptions = medicalRecord.getPrescriptions();
        this.testResults = medicalRecord.getTestResults();
        this.createdAt = medicalRecord.getCreatedAt();
        this.updatedAt = medicalRecord.getUpdatedAt();
    }

    // Getters & Setters
    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Long diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Date getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(Date diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }
}
