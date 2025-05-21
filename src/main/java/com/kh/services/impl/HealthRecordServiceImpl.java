package com.kh.services.impl;

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
    public PatientProfileDTO doctorGetHealthRecordByPatientId(Long patientId, String doctorUsername) {
        User doctor = userRepository.getDoctorByUsername(doctorUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản bác sĩ không tồn tại!"));

        User patient = userRepository.getUserById(patientId)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy bệnh nhân!"));

        if (!this.healthRecordRepository.existsAppointmentBetweenDoctorAndPatient(doctor.getId(), patient.getId())) {
            throw new RuntimeException("Bác sĩ chỉ được xem hồ sơ của bệnh nhân đã có lịch hẹn");
        }

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
                .orElse(null);

        return PatientProfileDTO.fromEntities(patient, healthRecord);
    }

    @Override
    public PatientProfileDTO updateHealthRecord(Long patientId, PatientProfileDTO dto) {
        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ sức khỏe"));

        // Cập nhật các trường trong healthRecord từ dto
        healthRecord.setMedicalHistory(dto.getMedicalHistory());
        healthRecord.setAllergies(dto.getAllergies());
        healthRecord.setChronicConditions(dto.getChronicConditions());
        healthRecord.setWeight(dto.getWeight());
        healthRecord.setHeight(dto.getHeight());
        healthRecord.setBloodPressure(dto.getBloodPressure());
        healthRecord.setBloodSugar(dto.getBloodSugar());
        healthRecord.setUpdatedAt(new Date());

        healthRecordRepository.save(healthRecord);

        User user = userRepository.getUserById(patientId)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy người dùng!"));
        return PatientProfileDTO.fromEntities(user, healthRecord);
    }
}

