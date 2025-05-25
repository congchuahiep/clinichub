package com.kh.repositories;

import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;

import java.util.List;
import java.util.Optional;

public interface HealthRecordRepository extends GenericRepository<HealthRecord, User> {
    Optional<HealthRecord> findByPatient(User patient);
}
    