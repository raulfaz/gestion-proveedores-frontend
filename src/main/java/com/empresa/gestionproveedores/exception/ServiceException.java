package com.empresa.gestionproveedores.exception;

/**
 * Excepción personalizada para errores de servicios
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}