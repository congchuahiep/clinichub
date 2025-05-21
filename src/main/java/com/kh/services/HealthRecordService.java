package com.kh.services;

import com.kh.dtos.HealthRecordDTO;
import com.kh.dtos.PatientProfileDTO;

public interface HealthRecordService {
    HealthRecordDTO getHealthRecord(String patientUsername);

    HealthRecordDTO putHealthRecord(String patientUsername, HealthRecordDTO healthRecordDTO);

    PatientProfileDTO getPatientProfile(Long patientId, String doctorUsername);

    HealthRecordDTO updateHealthRecord(Long patientId, String doctorUsername, HealthRecordDTO healthRecordDTO);
}
