package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferir información de Orden de Compra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdenCompraDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String numeroOrden;
    private LocalDate fechaOrden;
    private LocalDate fechaEntrega;
    private String estado;
    private BigDecimal totalOrden;
    private String observaciones;
    private Long proveedorId;
    private String proveedorRazonSocial;
    private LocalDateTime fechaRegistro;
    private List<DetalleOrdenDTO> detalles;
}