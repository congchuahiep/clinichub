package com.kh.controllers.api;

import com.kh.dtos.EmailDTO;
import com.kh.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping()
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
