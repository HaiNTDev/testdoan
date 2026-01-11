package com.example.Automated.Application.Mangament.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    //User error - 60x
    UNCATEGORIZED_ERROR(999, "Uncategorized error"),
    USER_NOT_FOUND(600, "User not found"),
    USER_EXISTS(601, "User already exists"),
    USER_UNAUTHORIZED(602, "User doesn't have permission to perform this action "),
    USER_ENROLLED_EMPTY(603, "User hasn't enrolled in any courses "),

    //Register error - 61x
    USERNAME_EXISTS(610, "Username already exists"),

    //email error - 65X
    EMAIL_UNAUTHENTICATED(650, "Please check your email to verify your account"),
    EMAIL_NOT_FOUND(651, "Email not found"),
    EMAIL_INVALID(652, "Email invalid"),
    EMAIL_EXISTS(654, "Email already exists"),
    EMAIL_CAN_NOT_SEND(655, "Email can not send"),
    EMAIL_NULL(656, "Email can not be null"),
    ROLE_INVALID(620, "Role không hợp lệ"),
    ROLE_NOT_FOUND(621, "Role không tồn tại trong hệ thống"),
    DEPARTMENT_REQUIRED(622, "Department là bắt buộc với role này"),
    DEPARTMENT_NOT_FOUND(623, "Không tìm thấy khoa: %s"),
    POSITION_NOT_FOUND(624, "Không tìm thấy vị trí: %s"),
    POSITION_NOT_IN_DEPARTMENT(625, "Vị trí không thuộc khoa đã chọn"),
    PASSWORD_INVALID(626, "Password must be at least 6 characters"),
    USERNAME_INVALID(611, "Invalid username format"),
    GMAIL_INVALID(653, "Invalid gmail format"),
    DUPLICATE_USERNAME_IN_BATCH(612, "Duplicate username in import batch"),
    DUPLICATE_EMAIL_IN_BATCH(657, "Duplicate email in import batch"),
    //OTP error - 66X
    OTP_INVALID(660, "Wrong OTP"),
    OTP_EXPIRED(661, "OTP expired"),
    OTP_NOT_FOUND(662, "OTP not found");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;

    }
}
