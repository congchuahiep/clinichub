package com.kh.repositories;

import com.kh.pojo.HealthRecord;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository {

    HealthRecord save(HealthRecord healthRecord);

    HealthRecord update(HealthRecord healthRecord);

    Optional<HealthRecord> findById(Long id);

    List<HealthRecord> list();

    void delete(Long id);
}
    