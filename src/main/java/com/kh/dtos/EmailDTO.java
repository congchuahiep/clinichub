package com.kh.dtos;

import jakarta.validation.constraints.NotNull;

public class EmailDTO {
    @NotNull(message = "Email can't null")
    private String toEmail;
    @NotNull(message = "Subject can't null")
    private String subject;
    @NotNull(message = "Body can't null")
    private String body;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toemail) {
        this.toEmail = toemail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
