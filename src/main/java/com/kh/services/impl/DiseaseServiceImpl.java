package com.kh.services.impl;

import java.util.List;
import java.util.Map;

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
    public List<DiseaseDTO> getDiseaseList(Map<String, String> params) {
        List<Disease> diseases = diseaseRepository.list(params);

        return diseases.stream()
                .map(disease -> new DiseaseDTO(disease.getId(), disease.getName(), disease.getDescription()))
                .toList();
    }
}
