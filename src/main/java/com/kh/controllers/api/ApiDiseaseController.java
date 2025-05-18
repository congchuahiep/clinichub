package com.kh.controllers.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dtos.DiseaseDTO;
import com.kh.services.DiseaseService;

@RestController
@RequestMapping("/api")
public class ApiDiseaseController {
    @Autowired
    private DiseaseService diseaseService;

    @GetMapping("/diseases")
    public ResponseEntity<?> getDisease(@RequestParam Map<String, String> params) {
        List<DiseaseDTO> diseaseDTOs = this.diseaseService.getDiseaseList(params);

        return ResponseEntity.ok(diseaseDTOs);
    }
}
