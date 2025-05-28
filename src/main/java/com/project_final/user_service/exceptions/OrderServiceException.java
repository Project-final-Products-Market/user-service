package com.project_final.user_service.exceptions;

/**
 * Excepción lanzada cuando hay errores al comunicarse con el servicio de órdenes
 */
public class OrderServiceException extends UserServiceException {

    private static final String DEFAULT_ERROR_CODE = "ORDER_SERVICE_ERROR";

    public OrderServiceException(String message) {
        super(message, DEFAULT_ERROR_CODE);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, DEFAULT_ERROR_CODE, cause);
    }

    public OrderServiceException(Long userId, Throwable cause) {
        super("Error al obtener órdenes del usuario con ID: " + userId, DEFAULT_ERROR_CODE, cause);
    }
}
