package com.gialong.classroom.service.impl;

import com.gialong.classroom.dto.auth.*;
import com.gialong.classroom.dto.email.NotificationEvent;
import com.gialong.classroom.dto.user.OutboundUserResponse;
import com.gialong.classroom.dto.user.UserResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Role;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.httpclient.OutboundIdentityClient;
import com.gialong.classroom.repository.UserRepository;
import com.gialong.classroom.repository.httpclient.OutboundUserClient;
import com.gialong.classroom.service.AuthService;
import com.gialong.classroom.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl extends BaseRedisServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserClient outboundUserClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${outbound.identity.client-id}")
    private String CLIENT_ID;

    @Value("${outbound.identity.client-secret}")
    private String CLIENT_SECRET;

    @Value("${outbound.identity.redirect-uri}")
    private String REDIRECT_URI;

    public AuthServiceImpl(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtService jwtService,
                           AuthenticationManager authenticationManager, OutboundIdentityClient outboundIdentityClient,
                           OutboundUserClient outboundUserClient, KafkaTemplate<String, Object> kafkaTemplate) {
        super(redisTemplate);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.outboundIdentityClient = outboundIdentityClient;
        this.outboundUserClient = outboundUserClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public AuthResponse outboundAuthenticate(String code){
        String GRANT_TYPE = "authorization_code";
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());
        //onboard user (get user info)
        OutboundUserResponse userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        User user = userRepository.findByUsername(userInfo.getEmail())
                .orElse(User.builder()
                        .username(userInfo.getEmail())
                        .firstName(userInfo.getGivenName())
                        .lastName(userInfo.getFamilyName())
                        .email(userInfo.getEmail())
                        .avatarUrl(userInfo.getPicture())
                        .role(Role.STUDENT)
                        .build()
                );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public User register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = (request.getRole() == null || request.getRole().isEmpty()) ?
                Role.STUDENT :
                Objects.equals(request.getRole(), "STUDENT") ?
                        Role.STUDENT :
                        Role.TEACHER;
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .role(role)
                .build();

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Welcome to our classroom")
                .body("Hello, have a good day with our classroom. Love you, " + request.getUsername())
                .build();

        // Publish message to kafka
        kafkaTemplate.send("send-email", notificationEvent);

        return userRepository.save(user);

    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(String refreshToken){
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!Objects.equals(refreshToken, user.getRefreshToken()) || StringUtils.isBlank(user.getRefreshToken()))
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);

        try {
            boolean isValidToken = jwtService.verificationToken(refreshToken, user);
            if (!isValidToken) {
                throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            String jwtId = SignedJWT.parse(refreshToken).getJWTClaimsSet().getJWTID();
            long accessTokenExp = jwtService.extractTokenExpired(refreshToken);
            this.set(jwtId, refreshToken);
            this.setTimeToLive(jwtId, accessTokenExp, TimeUnit.MILLISECONDS);

            return AuthResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public void signOut(@Valid LogoutRequest request) {
        String username = jwtService.extractUsername(request.getAccessToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        if(accessTokenExp > 0) {
            try {
                String jwtId = SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
                this.set(jwtId, request.getAccessToken());
                this.setTimeToLive(jwtId, accessTokenExp, TimeUnit.MILLISECONDS);
                user.setRefreshToken(null);
                userRepository.save(user);
            } catch (ParseException e) {
                throw new AppException(ErrorCode.SIGN_OUT_FAILED);
            }
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();
    }
}