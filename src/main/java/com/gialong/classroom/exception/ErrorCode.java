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
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    CLASS_NOT_FOUND(400, "Class not found", HttpStatus.BAD_REQUEST),
    ALREADY_ENROLLED_CLASS(400, "Already Enrolled Class", HttpStatus.BAD_REQUEST),
    NOT_ENROLLED_CLASS(400, "Not Enrolled Class", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND(404, "Post not found", HttpStatus.NOT_FOUND),
    ASSIGNMENT_NOT_FOUND(404, "Assignment not found", HttpStatus.NOT_FOUND),
    SUBMISSION_NOT_FOUND(404, "Submission not found", HttpStatus.NOT_FOUND),
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
