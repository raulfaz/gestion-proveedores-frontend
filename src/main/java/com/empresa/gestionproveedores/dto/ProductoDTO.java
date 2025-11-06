package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferir información de Producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String unidadMedida;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private Long proveedorId;
    private String proveedorRazonSocial;
}