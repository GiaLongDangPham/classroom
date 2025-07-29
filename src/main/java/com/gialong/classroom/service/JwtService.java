package com.gialong.classroom.service;

import com.gialong.classroom.model.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String accessToken);

    boolean verificationToken(String token, User user) throws ParseException, JOSEException;

    long extractTokenExpired(String token);
}
