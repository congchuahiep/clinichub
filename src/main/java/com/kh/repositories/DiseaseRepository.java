package com.kh.repositories;

import java.util.List;
import java.util.Map;

import com.kh.pojo.Disease;

public interface DiseaseRepository {
    List<Disease> getDiseaseList(Map<String, String> params);
}
