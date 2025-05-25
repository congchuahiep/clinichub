package com.kh.services;

import com.kh.dtos.EmailDTO;

public interface EmailService {
    public void sendEmail(EmailDTO emailDTO);
}
