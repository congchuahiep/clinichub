package com.kh.repositories;

import com.kh.pojo.Specialty;

import java.util.Optional;

public interface SpecialtyRepository {
    Optional<Specialty> findById(Long id);
}
