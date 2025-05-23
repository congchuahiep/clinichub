package com.kh.services.impl;

import com.kh.dtos.SpecialtyDTO;
import com.kh.pojo.Specialty;
import com.kh.repositories.SpecialtyRepository;
import com.kh.services.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    public List<SpecialtyDTO> getSpecialtyList() {
        return specialtyRepository.findAll();
    }
}
