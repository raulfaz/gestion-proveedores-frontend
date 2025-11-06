package com.empresa.gestionproveedores.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Proveedor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String telefonoContacto;
    private Boolean activo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaRegistro;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;

    /**
     * Método auxiliar para mostrar en componentes
     */
    public String getNombreCompleto() {
        return razonSocial + (nombreComercial != null ? " (" + nombreComercial + ")" : "");
    }

    /**
     * Método para mostrar estado como texto
     */
    public String getEstadoTexto() {
        return activo != null && activo ? "Activo" : "Inactivo";
    }
}