package com.kh.services.impl;

import com.kh.dtos.HospitalDTO;
import com.kh.repositories.HospitalRepository;
import com.kh.services.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public List<HospitalDTO> getHospitalList() {
        return hospitalRepository.list()
                .stream()
                .map(HospitalDTO::new)
                .toList();
    }
}
