package com.empresa.gestionproveedores.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para Usuario del Sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private String rol;
    private boolean activo;
}