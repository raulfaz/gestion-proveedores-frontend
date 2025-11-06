package com.empresa.gestionproveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para envolver las respuestas de la API REST
 * @param <T> Tipo de dato contenido en la respuesta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;
    private String error;

    /**
     * Constructor para respuestas exitosas con datos
     */
    public ApiResponseDTO(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    /**
     * Constructor para respuestas exitosas sin datos
     */
    public ApiResponseDTO(String message) {
        this.success = true;
        this.message = message;
    }

    /**
     * Constructor para respuestas de error
     */
    public static <T> ApiResponseDTO<T> error(String error) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setError(error);
        response.setMessage("Error en la operación");
        return response;
    }
}