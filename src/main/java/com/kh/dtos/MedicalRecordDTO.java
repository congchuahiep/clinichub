package com.kh.dtos;

import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicalRecordDTO {

    private Long id;                      // ID của bản ghi chẩn đoán

    @NotNull
    private Long healthRecordId;          // ID của hồ sơ sức khỏe

    @NotNull
    private Long doctorId;                // ID của bác sĩ chẩn đoán

    @NotNull
    private Long appointmentId;           // ID của lịch khám

    @NotNull
    private Long diseaseId;               // ID của bệnh (nếu có)

    @NotBlank
    private String diagnosis;             // Mô tả chẩn đoán

    @NotNull
    private Date diagnosisDate;           // Ngày chẩn đoán

    private String prescriptions;         // Đơn thuốc (có thể rỗng)

    private String testResults;           // Kết quả xét nghiệm (có thể rỗng)

    private String notes;                 // Ghi chú của bác sĩ (có thể rỗng)

    private Date createdAt;               // Ngày tạo bản ghi

    private Date updatedAt;               // Ngày cập nhật bản ghi

    // Constructor rỗng
    public MedicalRecordDTO() {}

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
