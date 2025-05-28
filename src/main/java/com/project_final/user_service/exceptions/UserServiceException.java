package com.project_final.user_service.exceptions;

/**
 * Excepción base para todas las excepciones del servicio de usuarios
 */
public abstract class UserServiceException extends RuntimeException {

    protected String errorCode;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public UserServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
