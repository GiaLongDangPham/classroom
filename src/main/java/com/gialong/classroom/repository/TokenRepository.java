package com.gialong.classroom.repository;

import com.gialong.classroom.model.Token;
import com.gialong.classroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByRefreshToken(String refreshToken);
    List<Token> findByUser(User user);
    Token findByToken(String token);
}