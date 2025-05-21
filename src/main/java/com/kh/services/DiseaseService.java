package com.kh.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kh.dtos.DiseaseDTO;

public interface DiseaseService {
    List<DiseaseDTO> getDiseaseList(Map<String, String> params);
}
