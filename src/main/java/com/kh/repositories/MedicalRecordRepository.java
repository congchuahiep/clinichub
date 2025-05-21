package com.kh.repositories;

import com.kh.pojo.MedicalRecord;

public interface MedicalRecordRepository {
    MedicalRecord save(MedicalRecord medicalRecord);
}
