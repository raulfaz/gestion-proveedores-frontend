package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para Detalle de Orden de Compra (alineado al backend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetalleOrdenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    private Long productoId;
    private ProductoResponseDTO producto; // opcional para mostrar nombre/código

    private Long ordenCompraId;
}