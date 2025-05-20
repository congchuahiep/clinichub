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

    private Long id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String phone;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date birthDate;

    @NotBlank
    private String gender;

    private String address;

    private String avatar;

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

    // Bạn có thể tạo thêm constructor nhận User và HealthRecord hoặc builder

    // Getters & Setters

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
    
    public static PatientProfileDTO fromEntities(User user, HealthRecord healthRecord) {
        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setAvatar(user.getAvatar());

        if (healthRecord != null) {
            dto.setMedicalHistory(healthRecord.getMedicalHistory());
            dto.setAllergies(healthRecord.getAllergies());
            dto.setChronicConditions(healthRecord.getChronicConditions());
            dto.setWeight(healthRecord.getWeight());
            dto.setHeight(healthRecord.getHeight());
            dto.setBloodPressure(healthRecord.getBloodPressure());
            dto.setBloodSugar(healthRecord.getBloodSugar());
        }

        return dto;
    }

}
