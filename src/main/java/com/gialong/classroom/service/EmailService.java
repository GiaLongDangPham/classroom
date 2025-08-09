package com.gialong.classroom.service;

import com.gialong.classroom.dto.email.SendEmailRequest;

public interface EmailService {

    void sendEmail(SendEmailRequest request);
}
