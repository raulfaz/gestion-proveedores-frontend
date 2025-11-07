package com.empresa.gestionproveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO simplificado para Producto (alineado al backend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String codigo;
    private String nombre;
    private String unidadMedida;
    private BigDecimal precio;
    private Boolean activo;
}