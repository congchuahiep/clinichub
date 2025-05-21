package com.kh.repositories;

import com.kh.pojo.HealthRecord;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository {

    Optional<HealthRecord> findById(Long id);

    List<HealthRecord> findAll();

    HealthRecord save(HealthRecord healthRecord);

    void delete(Long id);
}
    