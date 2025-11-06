package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para transferir información de Proveedor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProveedorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private Integer cantidadProductos;
    private Integer cantidadOrdenes;
}