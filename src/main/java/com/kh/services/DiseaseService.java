package com.kh.services;

import java.util.Map;

import com.kh.dtos.DiseaseDTO;
import com.kh.utils.PaginatedResult;

public interface DiseaseService {
    PaginatedResult<DiseaseDTO> getDiseaseList(Map<String, String> params);
}
