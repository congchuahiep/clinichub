package com.kh.repositories;

import com.kh.pojo.MedicalRecord;

import java.util.List;
import java.util.Map;

public interface MedicalRecordRepository {
    MedicalRecord save(MedicalRecord medicalRecord);

    List<MedicalRecord> findByPatientId(Long patientId, int page, int pageSize);

    Long countByPatientId(Long patientId);
}
