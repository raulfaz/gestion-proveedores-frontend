package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String error) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
}