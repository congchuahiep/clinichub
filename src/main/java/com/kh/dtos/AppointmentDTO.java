package com.kh.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kh.pojo.Appointment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class AppointmentDTO {
    private Long id;

    @NotNull(message = "ID bác sĩ không được để trống")
    private Long doctorId;

    @NotNull(message = "Thời gian khám không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @NotNull(message = "Ca khám không được để trống!")
    @Min(value = 1, message = "Ca khám không hợp lệ! Chọn ca 1-16")
    @Max(value = 16, message = "Ca khám không hợp lệ! Chọn ca 1-16")
    private int timeSlot;  // client chỉ gửi số 1–16

    @Size(max = 65535, message = "Ghi chú không được vượt quá 65535 ký tự")
    private String note;

    // Các trường này chỉ để xuất
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    private UserDTO doctor;

    private UserDTO patient;

    // Constructor
    public AppointmentDTO() {
    }

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.appointmentDate = appointment.getAppointmentDate();
        this.note = appointment.getNote();
        this.status = appointment.getStatus();
        this.createdAt = appointment.getCreatedAt();
        this.timeSlot = appointment.getTimeSlot().getSlotNumber();
        this.doctor = new UserDTO(appointment.getDoctorId());
        this.patient = new UserDTO(appointment.getPatientId());
    }

    // getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(UserDTO doctor) {
        this.doctor = doctor;
    }

    public UserDTO getPatient() {
        return patient;
    }

    public void setPatient(UserDTO patient) {
        this.patient = patient;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }
}