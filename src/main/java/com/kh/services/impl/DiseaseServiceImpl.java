package com.kh.services.impl;

import java.util.Map;

import com.kh.utils.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dtos.DiseaseDTO;
import com.kh.pojo.Disease;
import com.kh.repositories.DiseaseRepository;
import com.kh.services.DiseaseService;

@Service
public class DiseaseServiceImpl implements DiseaseService {

    @Autowired
    DiseaseRepository diseaseRepository;

    @Override
    public PaginatedResult<DiseaseDTO> getDiseaseList(Map<String, String> params) {

        PaginatedResult<Disease> diseases = diseaseRepository.paginatedList(params);

        return diseases.mapTo(DiseaseDTO::new);
    }
}
