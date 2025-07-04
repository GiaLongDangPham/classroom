package com.gialong.classroom.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(400, "User not existed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    ACCESS_DINED(403, "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(400, "Token invalid", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
