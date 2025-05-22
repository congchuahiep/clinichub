package com.kh.repositories.impl;

import com.kh.dtos.MedicalRecordDTO;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.MedicalRecordRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MedicalRecordRepositoryImpl extends AbstractRepository implements MedicalRecordRepository {
    @Override
    public Optional<MedicalRecordDTO> findById(Long id) {
        return Optional.ofNullable(getCurrentSession().get(MedicalRecordDTO.class, id));
    }
}