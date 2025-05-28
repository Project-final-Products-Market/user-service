package com.project_final.user_service.exceptions;

/**
 * Excepción lanzada cuando hay errores de validación en los datos del usuario
 */
public class UserValidationException extends UserServiceException {

    private static final String DEFAULT_ERROR_CODE = "USER_VALIDATION_ERROR";

    public UserValidationException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public UserValidationException(String field, String reason) {
        super("Error de validación en campo '" + field + "': " + reason, DEFAULT_ERROR_CODE);
    }

    public UserValidationException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, cause);
    }
}
