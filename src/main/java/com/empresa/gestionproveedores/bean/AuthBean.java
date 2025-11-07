package com.empresa.gestionproveedores.bean;

import com.empresa.gestionproveedores.dto.UsuarioDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean de Autenticación y Sesión
 */
@Named("authBean")
@SessionScoped
@Slf4j
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private UsuarioDTO usuarioActual;

    @Getter
    @Setter
    private boolean autenticado;

    // Usuarios de demostración (En producción esto vendría de BD)
    private Map<String, UsuarioDTO> usuarios;

    @PostConstruct
    public void init() {
        autenticado = false;
        inicializarUsuarios();
    }

    /**
     * Inicializa usuarios de demostración
     */
    private void inicializarUsuarios() {
        usuarios = new HashMap<>();

        // Usuario Administrador
        usuarios.put("admin", UsuarioDTO.builder()
                .id(1L)
                .username("admin")
                .password("admin123")
                .nombreCompleto("Administrador del Sistema")
                .email("admin@gestionproveedores.com")
                .rol("ADMIN")
                .activo(true)
                .build());

        // Usuario Normal
        usuarios.put("user", UsuarioDTO.builder()
                .id(2L)
                .username("user")
                .password("user123")
                .nombreCompleto("Usuario Demo")
                .email("user@gestionproveedores.com")
                .rol("USER")
                .activo(true)
                .build());

        // Usuario para Entrevista
        usuarios.put("entrevista", UsuarioDTO.builder()
                .id(3L)
                .username("entrevista")
                .password("demo2025")
                .nombreCompleto("Usuario Entrevista")
                .email("entrevista@gestionproveedores.com")
                .rol("ADMIN")
                .activo(true)
                .build());
    }

    /**
     * Realiza el login del usuario
     */
    public void login() {
        try {
            log.info("Intento de login para usuario: {}", username);

            if (username == null || username.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia",
                        "Por favor ingrese su nombre de usuario");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia",
                        "Por favor ingrese su contraseña");
                return;
            }

            UsuarioDTO usuario = usuarios.get(username.toLowerCase());

            if (usuario != null && usuario.getPassword().equals(password)) {
                if (!usuario.isActivo()) {
                    mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error",
                            "Usuario desactivado. Contacte al administrador");
                    return;
                }

                usuarioActual = usuario;
                autenticado = true;

                log.info("Login exitoso para: {}", username);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Éxito",
                        "¡Bienvenido " + usuario.getNombreCompleto() + "!");

                // Redirigir al dashboard
                redirigir("/index.xhtml");
            } else {
                log.warn("Credenciales inválidas para: {}", username);
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error",
                        "Usuario o contraseña incorrectos");
                limpiarCampos();
            }
        } catch (Exception e) {
            log.error("Error durante el login", e);
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error",
                    "Error al procesar el login: " + e.getMessage());
        }
    }

    /**
     * Cierra la sesión del usuario
     */
    public void logout() {
        try {
            log.info("Logout para usuario: {}", usuarioActual != null ?
                    usuarioActual.getUsername() : "desconocido");

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            // Invalidar sesión
            externalContext.invalidateSession();

            // Redirigir al login
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            log.error("Error durante logout", e);
        }
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean tieneRol(String rol) {
        return autenticado && usuarioActual != null &&
                usuarioActual.getRol().equals(rol);
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin() {
        return tieneRol("ADMIN");
    }

    /**
     * Obtiene el nombre para mostrar del usuario actual
     */
    public String getNombreUsuario() {
        if (usuarioActual != null) {
            return usuarioActual.getNombreCompleto();
        }
        return "Invitado";
    }

    /**
     * Obtiene las iniciales del usuario
     */
    public String getInicialesUsuario() {
        if (usuarioActual != null && usuarioActual.getNombreCompleto() != null) {
            String[] partes = usuarioActual.getNombreCompleto().split(" ");
            if (partes.length >= 2) {
                return partes[0].substring(0, 1) + partes[1].substring(0, 1);
            } else if (partes.length == 1) {
                return partes[0].substring(0, Math.min(2, partes[0].length()));
            }
        }
        return "??";
    }

    /**
     * Limpia los campos del formulario
     */
    private void limpiarCampos() {
        username = null;
        password = null;
    }

    /**
     * Muestra un mensaje en la interfaz
     */
    private void mostrarMensaje(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    /**
     * Redirige a una página
     */
    private void redirigir(String pagina) {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath() + pagina);
        } catch (IOException e) {
            log.error("Error al redirigir", e);
        }
    }
}