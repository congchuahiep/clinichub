package com.kh.services;

import com.kh.dtos.HealthRecordDTO;
import com.kh.dtos.PatientProfileDTO;

public interface HealthRecordService {
    PatientProfileDTO getPatientProfile(Long patientId, String doctorUsername);

    HealthRecordDTO updateHealthRecord(Long patientId, String doctorUsername, HealthRecordDTO healthRecordDTO);
}
