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
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para Orden de Compra (alineado al backend)
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

    private String estado; // PENDIENTE/APROBADA/RECIBIDA/CANCELADA

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    private String observaciones;

    private Long proveedorId;
    private ProveedorResponseDTO proveedor; // para mostrar razonSocial

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private List<DetalleOrdenDTO> detalles = new ArrayList<>();
}