package com.kh.services.impl;

import com.kh.dtos.PatientProfileDTO;
import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import com.kh.repositories.HealthRecordRepository;
import com.kh.repositories.UserRepository;
import com.kh.services.HealthRecordService;

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
    public PatientProfileDTO getHealthRecord(Long doctorId, Long patientId) {
        boolean hasAccess = healthRecordRepository.existsAppointmentBetweenDoctorAndPatient(doctorId, patientId);
        if (!hasAccess) {
            return null;
        }

        Optional<User> patientOpt = userRepository.findById(patientId);
        if (!patientOpt.isPresent()) return null;

        Optional<HealthRecord> healthRecordOpt = healthRecordRepository.findByPatientId(patientId);

        return PatientProfileDTO.fromEntities(patientOpt.get(), healthRecordOpt.orElse(null));
    }

    @Override
    public PatientProfileDTO updateHealthRecord(Long doctorId, Long patientId, PatientProfileDTO updatedDTO) {
        boolean hasAccess = healthRecordRepository.existsAppointmentBetweenDoctorAndPatient(doctorId, patientId);
        if (!hasAccess) {
            return null;
        }

        Optional<User> patientOpt = userRepository.findById(patientId);
        if (!patientOpt.isPresent()) return null;
        User patient = patientOpt.get();

        HealthRecord healthRecord = healthRecordRepository.findByPatientId(patientId)
                .orElse(new HealthRecord());
        healthRecord.setPatientId(patient);

        healthRecord.setMedicalHistory(updatedDTO.getMedicalHistory());
        healthRecord.setAllergies(updatedDTO.getAllergies());
        healthRecord.setChronicConditions(updatedDTO.getChronicConditions());
        healthRecord.setWeight(updatedDTO.getWeight());
        healthRecord.setHeight(updatedDTO.getHeight());
        healthRecord.setBloodPressure(updatedDTO.getBloodPressure());
        healthRecord.setBloodSugar(updatedDTO.getBloodSugar());

        healthRecordRepository.save(healthRecord);

        return PatientProfileDTO.fromEntities(patient, healthRecord);
    }
}
