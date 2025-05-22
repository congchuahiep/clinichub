package com.kh.dtos;

import java.util.Date;

public class AppointmentDetailsDTO {
    private AppointmentDTO appointment; // Thông tin lịch khám
    private MedicalRecordDTO medicalRecord; // Thông tin bản ghi khám nếu có

    // Constructors, getters, setters
    public AppointmentDetailsDTO() {}

    public AppointmentDetailsDTO(AppointmentDTO appointment, MedicalRecordDTO medicalRecord) {
        this.appointment = appointment;
        this.medicalRecord = medicalRecord;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }

    public MedicalRecordDTO getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecordDTO medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
}