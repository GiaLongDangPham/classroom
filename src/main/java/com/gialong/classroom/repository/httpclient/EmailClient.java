package com.gialong.classroom.repository.httpclient;

import com.gialong.classroom.dto.email.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "email-client", url = "${notification.email.brevo-url}")
public interface EmailClient {
    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    void sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailRequest body);
}
