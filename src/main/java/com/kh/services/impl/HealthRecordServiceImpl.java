package com.kh.services.impl;

import com.kh.dtos.HealthRecordDTO;
import com.kh.dtos.PatientProfileDTO;
import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import com.kh.repositories.HealthRecordRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.HealthRecordService;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public HealthRecordDTO getHealthRecord(String patientUsername) {
        User patient = userRepository.getUserByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy bệnh nhân!"));

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi! Bệnh nhân này không có hồ sơ sức khoẻ?"));

        return new HealthRecordDTO(healthRecord);
    }

    @Override
    public HealthRecordDTO putHealthRecord(String patientUsername, HealthRecordDTO healthRecordDTO) {
        User patient = userRepository.getUserByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy bệnh nhân!"));

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi! Bệnh nhân này không có hồ sơ sức khoẻ?"));

        // Cập nhật các trường trong healthRecord từ dto
        healthRecord.setMedicalHistory(healthRecordDTO.getMedicalHistory());
        healthRecord.setAllergies(healthRecordDTO.getAllergies());
        healthRecord.setChronicConditions(healthRecordDTO.getChronicConditions());
        healthRecord.setWeight(healthRecordDTO.getWeight());
        healthRecord.setHeight(healthRecordDTO.getHeight());
        healthRecord.setBloodPressure(healthRecordDTO.getBloodPressure());
        healthRecord.setBloodSugar(healthRecordDTO.getBloodSugar());
        healthRecord.setUpdatedAt(new Date());

        // Lưu lại
        healthRecordRepository.save(healthRecord);

        return new HealthRecordDTO(healthRecord);
    }

    @Override
    public PatientProfileDTO getPatientProfile(Long patientId, String doctorUsername) {
        User doctor = userRepository.getDoctorByUsername(doctorUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản bác sĩ không tồn tại!"));

        User patient = userRepository.getUserById(patientId)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy bệnh nhân!"));

        if (!this.healthRecordRepository.existsAppointmentBetweenDoctorAndPatient(doctor.getId(), patient.getId())) {
            throw new RuntimeException("Bác sĩ chỉ được xem hồ sơ của bệnh nhân đã có lịch hẹn");
        }

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Lỗi! Bệnh nhân này không có hồ sơ sức khoẻ?"));

        return new PatientProfileDTO(patient, healthRecord);
    }

    @Override
    public HealthRecordDTO updateHealthRecord(Long patientId, String doctorUsername, HealthRecordDTO healthRecordDTO) {
        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ sức khỏe"));

        User doctor = userRepository.getDoctorByUsername(doctorUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản bác sĩ không tồn tại!"));

        if (!this.healthRecordRepository.existsAppointmentBetweenDoctorAndPatient(doctor.getId(), patientId)) {
            throw new RuntimeException("Bác sĩ chỉ được xem hồ sơ của bệnh nhân đã có lịch hẹn");
        }

        // Cập nhật các trường trong healthRecord từ dto
        healthRecord.setMedicalHistory(healthRecordDTO.getMedicalHistory());
        healthRecord.setAllergies(healthRecordDTO.getAllergies());
        healthRecord.setChronicConditions(healthRecordDTO.getChronicConditions());
        healthRecord.setWeight(healthRecordDTO.getWeight());
        healthRecord.setHeight(healthRecordDTO.getHeight());
        healthRecord.setBloodPressure(healthRecordDTO.getBloodPressure());
        healthRecord.setBloodSugar(healthRecordDTO.getBloodSugar());
        healthRecord.setUpdatedAt(new Date());

        // Lưu lại
        healthRecordRepository.save(healthRecord);

        return new HealthRecordDTO(healthRecord);

    }
}

