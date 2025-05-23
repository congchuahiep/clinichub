package com.kh.controllers.api;

import java.util.Map;

import com.kh.utils.PaginatedResult;
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

    /**
     *
     * @param params Các tham số URL
     * @return Danh sách các loại bệnh
     */
    @GetMapping("/diseases")
    public ResponseEntity<?> getDisease(@RequestParam Map<String, String> params) {
        PaginatedResult<DiseaseDTO> diseaseDTOs = this.diseaseService.getDiseaseList(params);

        return ResponseEntity.ok(diseaseDTOs);
    }
}
