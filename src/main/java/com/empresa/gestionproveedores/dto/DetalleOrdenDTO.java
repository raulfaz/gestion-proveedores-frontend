package com.empresa.gestionproveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para Detalle de Orden de Compra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ordenCompraId;

    // Producto
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;

    // Cantidades y precios
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;

    /**
     * Calcula el subtotal (cantidad * precio)
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    /**
     * Calcula el total (subtotal - descuento)
     */
    public void calcularTotal() {
        calcularSubtotal();
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            this.total = subtotal.subtract(descuento);
        } else {
            this.total = subtotal;
        }
    }

    /**
     * Método auxiliar para mostrar en tabla
     */
    public String getProductoDescripcion() {
        return productoCodigo + " - " + productoNombre;
    }
}