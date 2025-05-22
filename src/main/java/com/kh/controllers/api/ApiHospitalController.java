package com.kh.controllers.api;

import com.kh.dtos.HospitalDTO;
import com.kh.services.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/hospitals")
    public ResponseEntity<?> getHospitals() {
        List<HospitalDTO> dtos = hospitalService.getHospitalList();

        return ResponseEntity.ok(dtos);
    }
}
