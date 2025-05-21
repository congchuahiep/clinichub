package com.kh.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kh.pojo.Disease;

public interface DiseaseRepository {

    Optional<Disease> findById(Long id);

    List<Disease> getDiseaseList(Map<String, String> params);
}
