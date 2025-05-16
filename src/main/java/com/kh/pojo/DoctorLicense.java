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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author congchuahiep
 */
@Entity
@Table(name = "doctor_licenses")
@NamedQueries({
    @NamedQuery(name = "DoctorLicense.findAll", query = "SELECT d FROM DoctorLicense d"),
    @NamedQuery(name = "DoctorLicense.findById", query = "SELECT d FROM DoctorLicense d WHERE d.id = :id"),
    @NamedQuery(name = "DoctorLicense.findByLicenseNumber", query = "SELECT d FROM DoctorLicense d WHERE d.licenseNumber = :licenseNumber"),
    @NamedQuery(name = "DoctorLicense.findByIssuedDate", query = "SELECT d FROM DoctorLicense d WHERE d.issuedDate = :issuedDate"),
    @NamedQuery(name = "DoctorLicense.findByExpiryDate", query = "SELECT d FROM DoctorLicense d WHERE d.expiryDate = :expiryDate"),
    @NamedQuery(name = "DoctorLicense.findByStatus", query = "SELECT d FROM DoctorLicense d WHERE d.status = :status"),
    @NamedQuery(name = "DoctorLicense.findByVerifiedAt", query = "SELECT d FROM DoctorLicense d WHERE d.verifiedAt = :verifiedAt")})
public class DoctorLicense implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "license_number")
    private String licenseNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "issued_date")
    @Temporal(TemporalType.DATE)
    private Date issuedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @Size(max = 8)
    @Column(name = "status")
    private String status;
    @Column(name = "verified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedAt;
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Specialty specialtyId;
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User doctorId;
    @JoinColumn(name = "verified_by", referencedColumnName = "id")
    @ManyToOne
    private User verifiedBy;

    public DoctorLicense() {
    }

    public DoctorLicense(Long id) {
        this.id = id;
    }

    public DoctorLicense(Long id, String licenseNumber, Date issuedDate, Date expiryDate) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Specialty getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Specialty specialtyId) {
        this.specialtyId = specialtyId;
    }

    public User getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(User doctorId) {
        this.doctorId = doctorId;
    }

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
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
        if (!(object instanceof DoctorLicense)) {
            return false;
        }
        DoctorLicense other = (DoctorLicense) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kh.pojo.DoctorLicense[ id=" + id + " ]";
    }
    
}
