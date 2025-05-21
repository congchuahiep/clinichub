/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author congchuahiep
 */
@Entity
@Table(name = "health_records")
@NamedQueries({
        @NamedQuery(name = "HealthRecord.findAll", query = "SELECT h FROM HealthRecord h"),
        @NamedQuery(name = "HealthRecord.findById", query = "SELECT h FROM HealthRecord h WHERE h.id = :id"),
        @NamedQuery(name = "HealthRecord.findByCreatedAt", query = "SELECT h FROM HealthRecord h WHERE h.createdAt = :createdAt"),
        @NamedQuery(name = "HealthRecord.findByUpdatedAt", query = "SELECT h FROM HealthRecord h WHERE h.updatedAt = :updatedAt")})
public class HealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User patient;

    @Lob
    @Size(max = 65535)
    @Column(name = "medical_history")
    private String medicalHistory;
    @Lob
    @Size(max = 65535)
    @Column(name = "allergies")
    private String allergies;
    @Lob
    @Size(max = 65535)
    @Column(name = "chronic_conditions")
    private String chronicConditions;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "weight")
    private Float weight;
    @Column(name = "height")
    private Float height;
    @Size(max = 20)
    @Column(name = "blood_pressure")
    private String bloodPressure;
    @Size(max = 20)
    @Column(name = "blood_sugar")
    private String bloodSugar;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "healthRecordId")
    private Set<MedicalRecord> medicalRecordSet;

    public HealthRecord() {
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User user) {
        this.patient = user;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
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

    public Set<MedicalRecord> getMedicalRecordSet() {
        return medicalRecordSet;
    }

    public void setMedicalRecordSet(Set<MedicalRecord> medicalRecordSet) {
        this.medicalRecordSet = medicalRecordSet;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
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
