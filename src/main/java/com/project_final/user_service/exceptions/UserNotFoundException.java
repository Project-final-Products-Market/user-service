package com.project_final.user_service.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un usuario
 */
public class UserNotFoundException extends UserServiceException {

    private static final String DEFAULT_ERROR_CODE = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public UserNotFoundException(Long userId) {
        super("Usuario no encontrado con ID: " + userId, DEFAULT_ERROR_CODE);
    }

    public UserNotFoundException(String field, String value) {
        super("Usuario no encontrado con " + field + ": " + value, DEFAULT_ERROR_CODE);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, cause);
    }
}