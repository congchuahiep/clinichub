package com.kh.services;

import com.kh.dtos.MedicalRecordDTO;
import com.kh.dtos.PaginatedResponseDTO;

public interface MedicalRecordService {
    MedicalRecordDTO addMedicalRecord(MedicalRecordDTO medicalRecordDTO);

    PaginatedResponseDTO<MedicalRecordDTO> getMedicalRecords(Long patientId, int page, int pageSize);

    PaginatedResponseDTO<MedicalRecordDTO> doctorGetMedicalRecords(Long doctorId, Long patientId, int page, int pageSize);
}
