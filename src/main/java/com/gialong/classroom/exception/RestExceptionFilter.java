package com.gialong.classroom.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RestExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            response.setContentType("application/json");

            if(e instanceof AppException) {
                response.setStatus(((AppException) e).getErrorCode().getHttpStatus().value());
                response.getWriter().write(mapper.writeValueAsString(e));
            }
            else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(mapper.writeValueAsString(
                        new AppException(ErrorCode.UNCATEGORY)
                ));
            }
        }
    }
}