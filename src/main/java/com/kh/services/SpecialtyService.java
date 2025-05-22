package com.kh.services;

import com.kh.dtos.SpecialtyDTO;
import com.kh.pojo.Specialty;

import java.util.List;

public interface SpecialtyService {
    List<SpecialtyDTO> getSpecialtyList();
}
