package com.kh.services.impl;

import com.kh.dtos.PatientProfileDTO;
import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import com.kh.repositories.HealthRecordRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.HealthRecordService;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PatientProfileDTO getHealthRecordByPatientId(Long patientId) {
        User user = userRepository.getUserById(patientId);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy bệnh nhân");
        }

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
            .orElse(null);

        return PatientProfileDTO.fromEntities(user, healthRecord);
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

        User user = userRepository.getUserById(patientId);
        return PatientProfileDTO.fromEntities(user, healthRecord);
    }
}

