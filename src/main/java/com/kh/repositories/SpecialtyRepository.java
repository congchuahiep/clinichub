package com.kh.repositories;

import com.kh.dtos.SpecialtyDTO;
import com.kh.pojo.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository extends GenericRepository<Specialty, Long> {

    List<SpecialtyDTO> findAll();
}
