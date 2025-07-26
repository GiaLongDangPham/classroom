package com.gialong.classroom.service;

import com.gialong.classroom.dto.auth.AuthResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Token;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.TokenRepository;
import com.gialong.classroom.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final int MAX_TOKENS = 3;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;


    public void addToken(User user, String token, String refreshToken) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        //Nen uu tien xoa device ko phai Mobile (hien thuc sau)
        if (userTokens.size() >= MAX_TOKENS) {
            userTokens.stream().min(Comparator.comparing(Token::getExpirationDate)).ifPresent(tokenRepository::delete);
        }
        Token t = Token.builder()
                .token(token)
                .expirationDate(LocalDateTime.now().plusSeconds(expiration))
                .refreshToken(refreshToken)
                .refreshExpirationDate(LocalDateTime.now().plusSeconds(refreshExpiration))
                .expired(false)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(t);
    }

    public AuthResponse refresh(User user, String refreshToken) {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);

        if(existingToken == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (!jwtUtil.validateToken(refreshToken, user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        existingToken.setToken(newAccessToken);
        existingToken.setExpirationDate(LocalDateTime.now().plusSeconds(expiration));
        existingToken.setRefreshToken(newRefreshToken);
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(refreshExpiration));
        tokenRepository.save(existingToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
