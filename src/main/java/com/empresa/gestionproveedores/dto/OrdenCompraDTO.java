package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para Orden de Compra
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String numeroOrden;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaOrden;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEntregaEstimada;

    // Proveedor
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorRuc;

    // Estado
    private String estado; // PENDIENTE, APROBADA, RECIBIDA, CANCELADA

    // Montos
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal descuento;
    private BigDecimal total;

    // Información adicional
    private String observaciones;
    private String usuarioCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    // Detalles de la orden
    private List<DetalleOrdenDTO> detalles = new ArrayList<>();

    /**
     * Agrega un detalle a la orden
     */
    public void agregarDetalle(DetalleOrdenDTO detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        calcularTotales();
    }

    /**
     * Elimina un detalle de la orden
     */
    public void eliminarDetalle(DetalleOrdenDTO detalle) {
        if (this.detalles != null) {
            this.detalles.remove(detalle);
            calcularTotales();
        }
    }

    /**
     * Calcula los totales de la orden
     */
    public void calcularTotales() {
        if (detalles == null || detalles.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
            return;
        }

        // Calcular subtotal
        this.subtotal = detalles.stream()
                .map(detalle -> {
                    detalle.calcularTotal();
                    return detalle.getTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplicar descuento si existe
        BigDecimal totalConDescuento = this.subtotal;
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            totalConDescuento = totalConDescuento.subtract(descuento);
        }

        // Aplicar impuesto si existe (IVA 12%)
        if (impuesto != null && impuesto.compareTo(BigDecimal.ZERO) > 0) {
            totalConDescuento = totalConDescuento.add(impuesto);
        }

        this.total = totalConDescuento;
    }

    /**
     * Calcula el impuesto automáticamente (12% IVA)
     */
    public void calcularImpuesto() {
        if (subtotal != null) {
            this.impuesto = subtotal.multiply(new BigDecimal("0.12"));
        }
    }

    /**
     * Método para obtener el estado en español
     */
    public String getEstadoTexto() {
        if (estado == null) return "";

        switch (estado) {
            case "PENDIENTE":
                return "Pendiente";
            case "APROBADA":
                return "Aprobada";
            case "RECIBIDA":
                return "Recibida";
            case "CANCELADA":
                return "Cancelada";
            default:
                return estado;
        }
    }

    /**
     * Verifica si la orden puede ser editada
     */
    public boolean isEditable() {
        return "PENDIENTE".equals(estado);
    }

    /**
     * Verifica si la orden puede ser aprobada
     */
    public boolean isAprobable() {
        return "PENDIENTE".equals(estado);
    }

    /**
     * Verifica si la orden puede ser recibida
     */
    public boolean isRecibible() {
        return "APROBADA".equals(estado);
    }

    /**
     * Verifica si la orden puede ser cancelada
     */
    public boolean isCancelable() {
        return "PENDIENTE".equals(estado) || "APROBADA".equals(estado);
    }

    /**
     * Obtiene la cantidad total de ítems
     */
    public int getCantidadItems() {
        return detalles != null ? detalles.size() : 0;
    }
}