package com.kh.services;

import com.kh.dtos.PatientProfileDTO;

public interface HealthRecordService {
    PatientProfileDTO getHealthRecordByPatientId(Long patientId);
    PatientProfileDTO updateHealthRecord(Long patientId, PatientProfileDTO dto);
}

