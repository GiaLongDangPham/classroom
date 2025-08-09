package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.email.EmailRequest;
import com.gialong.classroom.dto.email.SendEmailRequest;
import com.gialong.classroom.dto.email.Sender;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.repository.httpclient.EmailClient;
import com.gialong.classroom.service.EmailService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    String apiKey;

    @Override
    public void sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Gia Long Dang Pham")
                        .email("longdpg.t1.2023@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
