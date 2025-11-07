package com.empresa.gestionproveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO simplificado para Proveedor (alineado al backend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private Boolean activo;
}