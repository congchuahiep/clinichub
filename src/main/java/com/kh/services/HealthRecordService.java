package com.kh.services;

import com.kh.dtos.PatientProfileDTO;

public interface HealthRecordService {

    PatientProfileDTO getHealthRecord(Long doctorId, Long patientId);

    PatientProfileDTO updateHealthRecord(Long doctorId, Long patientId, PatientProfileDTO updatedDTO);
}
