package com.kh.services;

import com.kh.dtos.PatientProfileDTO;
import org.springframework.security.core.Authentication;

public interface HealthRecordService {
    PatientProfileDTO doctorGetHealthRecordByPatientId(Long patientId, String doctorUsername);
    PatientProfileDTO updateHealthRecord(Long patientId, PatientProfileDTO dto);

}
