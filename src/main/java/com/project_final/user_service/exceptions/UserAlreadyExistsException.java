package com.project_final.user_service.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un usuario que ya existe
 */
public class UserAlreadyExistsException extends UserServiceException {

    private static final String DEFAULT_ERROR_CODE = "USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    // Método factory para crear excepción por email específico
    public static UserAlreadyExistsException forEmail(String email) {
        return new UserAlreadyExistsException("Ya existe un usuario con el email: " + email);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, cause);
    }
}
