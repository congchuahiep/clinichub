package com.kh.services;

import com.kh.dtos.MedicalRecordDTO;
import com.kh.utils.PaginatedResult;

public interface MedicalRecordService {
    MedicalRecordDTO addMedicalRecord(MedicalRecordDTO medicalRecordDTO);

    PaginatedResult<MedicalRecordDTO> getMedicalRecords(Long patientId, int page, int pageSize);

    PaginatedResult<MedicalRecordDTO> doctorGetMedicalRecords(Long doctorId, Long patientId, int page, int pageSize);
}
