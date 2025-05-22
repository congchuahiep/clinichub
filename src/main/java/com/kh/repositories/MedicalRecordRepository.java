package com.kh.repositories;

import com.kh.dtos.MedicalRecordDTO;

import java.util.Optional;

public interface MedicalRecordRepository {
    Optional<MedicalRecordDTO> findById(Long id);

}
