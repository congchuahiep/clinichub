/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kh.enums.UserRole;

/**
 *
 * @author congchuahiep
 */
@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findByPhone", query = "SELECT u FROM User u WHERE u.phone = :phone"),
        @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role = :role"),
        @NamedQuery(name = "User.findByIsActive", query = "SELECT u FROM User u WHERE u.isActive = :isActive"),
        @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
        @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
        @NamedQuery(name = "User.findByBirthDate", query = "SELECT u FROM User u WHERE u.birthDate = :birthDate"),
        @NamedQuery(name = "User.findByAddress", query = "SELECT u FROM User u WHERE u.address = :address"),
        @NamedQuery(name = "User.findByGender", query = "SELECT u FROM User u WHERE u.gender = :gender"),
        @NamedQuery(name = "User.findByAvatar", query = "SELECT u FROM User u WHERE u.avatar = :avatar"),
        @NamedQuery(name = "User.findByCreatedAt", query = "SELECT u FROM User u WHERE u.createdAt = :createdAt"),
        @NamedQuery(name = "User.findByUpdatedAt", query = "SELECT u FROM User u WHERE u.updatedAt = :updatedAt") })
public class User implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "username")
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email")
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "email")
    private String email;

    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive;

    @Size(max = 45)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 45)
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 6)
    @Column(name = "gender")
    private String gender;

    @Size(max = 100)
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonIgnore
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonIgnore
    private Date updatedAt;

    // CÁC TRƯỜNG QUAN HỆ NGƯỢC

    @ManyToMany(mappedBy = "userSet")
    private Set<Hospital> hospitalSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    private Set<Appointment> appointmentSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    private Set<Appointment> appointmentSet1;

    @OneToMany(mappedBy = "doctorId")
    private Set<MedicalRecord> medicalRecordSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "senderId")
    private Set<ChatMessage> chatMessageSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user1Id")
    private Set<Conversation> conversationSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user2Id")
    private Set<Conversation> conversationSet1;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    private Set<Review> reviewSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    private Set<Review> reviewSet1;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId")
    private Set<HealthRecord> healthRecordSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private Set<Notification> notificationSet;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    private Set<DoctorLicense> doctorLicenseSet;

    @OneToMany(mappedBy = "verifiedBy")
    private Set<DoctorLicense> doctorLicenseSet1;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String username, String password, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Set<Hospital> getHospitalSet() {
        return hospitalSet;
    }

    public void setHospitalSet(Set<Hospital> hospitalSet) {
        this.hospitalSet = hospitalSet;
    }

    public Set<Appointment> getAppointmentSet() {
        return appointmentSet;
    }

    public void setAppointmentSet(Set<Appointment> appointmentSet) {
        this.appointmentSet = appointmentSet;
    }

    public Set<Appointment> getAppointmentSet1() {
        return appointmentSet1;
    }

    public void setAppointmentSet1(Set<Appointment> appointmentSet1) {
        this.appointmentSet1 = appointmentSet1;
    }

    public Set<MedicalRecord> getMedicalRecordSet() {
        return medicalRecordSet;
    }

    public void setMedicalRecordSet(Set<MedicalRecord> medicalRecordSet) {
        this.medicalRecordSet = medicalRecordSet;
    }

    public Set<ChatMessage> getChatMessageSet() {
        return chatMessageSet;
    }

    public void setChatMessageSet(Set<ChatMessage> chatMessageSet) {
        this.chatMessageSet = chatMessageSet;
    }

    public Set<Conversation> getConversationSet() {
        return conversationSet;
    }

    public void setConversationSet(Set<Conversation> conversationSet) {
        this.conversationSet = conversationSet;
    }

    public Set<Conversation> getConversationSet1() {
        return conversationSet1;
    }

    public void setConversationSet1(Set<Conversation> conversationSet1) {
        this.conversationSet1 = conversationSet1;
    }

    public Set<Review> getReviewSet() {
        return reviewSet;
    }

    public void setReviewSet(Set<Review> reviewSet) {
        this.reviewSet = reviewSet;
    }

    public Set<Review> getReviewSet1() {
        return reviewSet1;
    }

    public void setReviewSet1(Set<Review> reviewSet1) {
        this.reviewSet1 = reviewSet1;
    }

    public Set<HealthRecord> getHealthRecordSet() {
        return healthRecordSet;
    }

    public void setHealthRecordSet(Set<HealthRecord> healthRecordSet) {
        this.healthRecordSet = healthRecordSet;
    }

    public Set<Notification> getNotificationSet() {
        return notificationSet;
    }

    public void setNotificationSet(Set<Notification> notificationSet) {
        this.notificationSet = notificationSet;
    }

    public Set<DoctorLicense> getDoctorLicenseSet() {
        return doctorLicenseSet;
    }

    public void setDoctorLicenseSet(Set<DoctorLicense> doctorLicenseSet) {
        this.doctorLicenseSet = doctorLicenseSet;
    }

    public Set<DoctorLicense> getDoctorLicenseSet1() {
        return doctorLicenseSet1;
    }

    public void setDoctorLicenseSet1(Set<DoctorLicense> doctorLicenseSet1) {
        this.doctorLicenseSet1 = doctorLicenseSet1;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.kh.pojo.User[ id=" + id + " ]";
    }

}
