package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para Producto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private Integer stockMinimo;
    private Integer stockActual;
    private Boolean activo;

    // Relación con Proveedor
    private Long proveedorId;
    private String proveedorNombre; // Para mostrar en tablas

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaRegistro;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    /**
     * Método para mostrar en componentes
     */
    public String getCodigoNombre() {
        return codigo + " - " + nombre;
    }

    /**
     * Método para mostrar estado como texto
     */
    public String getEstadoTexto() {
        return activo != null && activo ? "Activo" : "Inactivo";
    }

    /**
     * Verifica si el stock está bajo
     */
    public boolean isStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }

    /**
     * Precio formateado
     */
    public String getPrecioFormateado() {
        return precioUnitario != null ? "$" + precioUnitario.toString() : "$0.00";
    }
}