/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author congchuahiep
 */
@Entity
@Table(name = "medical_records")
@NamedQueries({
    @NamedQuery(name = "MedicalRecord.findAll", query = "SELECT m FROM MedicalRecord m"),
    @NamedQuery(name = "MedicalRecord.findById", query = "SELECT m FROM MedicalRecord m WHERE m.id = :id"),
    @NamedQuery(name = "MedicalRecord.findByDiagnosisDate", query = "SELECT m FROM MedicalRecord m WHERE m.diagnosisDate = :diagnosisDate"),
    @NamedQuery(name = "MedicalRecord.findByCreatedAt", query = "SELECT m FROM MedicalRecord m WHERE m.createdAt = :createdAt"),
    @NamedQuery(name = "MedicalRecord.findByUpdatedAt", query = "SELECT m FROM MedicalRecord m WHERE m.updatedAt = :updatedAt")})
public class MedicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Lob
    @Size(max = 65535)
    @Column(name = "diagnosis")
    private String diagnosis;
    @Column(name = "diagnosis_date")
    @Temporal(TemporalType.DATE)
    private Date diagnosisDate;
    @Lob
    @Size(max = 65535)
    @Column(name = "prescriptions")
    private String prescriptions;
    @Lob
    @Size(max = 65535)
    @Column(name = "test_results")
    private String testResults;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    @ManyToOne
    private Appointment appointmentId;
    @JoinColumn(name = "disease_id", referencedColumnName = "id")
    @ManyToOne
    private Disease diseaseId;
    @JoinColumn(name = "health_record_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private HealthRecord healthRecordId;
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne
    private User doctorId;

    public MedicalRecord() {
    }

    public MedicalRecord(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Appointment getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Appointment appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Disease getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Disease diseaseId) {
        this.diseaseId = diseaseId;
    }

    public HealthRecord getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(HealthRecord healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public User getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(User doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalRecord)) {
            return false;
        }
        MedicalRecord other = (MedicalRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kh.pojo.MedicalRecord[ id=" + id + " ]";
    }
    
}
