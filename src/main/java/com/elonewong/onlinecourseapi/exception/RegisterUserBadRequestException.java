package com.elonewong.onlinecourseapi.exception;

public class RegisterUserBadRequestException extends Exception {
    public RegisterUserBadRequestException() {
    }

    public RegisterUserBadRequestException(String message) {
        super(message);
    }
}
