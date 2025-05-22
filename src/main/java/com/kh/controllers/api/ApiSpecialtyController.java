package com.kh.controllers.api;


import com.kh.dtos.DiseaseDTO;
import com.kh.dtos.SpecialtyDTO;
import com.kh.services.DiseaseService;
import com.kh.services.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiSpecialtyController {
    @Autowired
    private SpecialtyService specialtyService;

    @GetMapping("/specialties")
    public ResponseEntity<?> getSpecialty() {
        List<SpecialtyDTO> specialtyDTOS = this.specialtyService.getSpecialtyList();

        return ResponseEntity.ok(specialtyDTOS);
    }
}
