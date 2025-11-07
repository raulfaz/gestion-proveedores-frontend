package com.empresa.gestionproveedores.bean;

import com.empresa.gestionproveedores.dto.ProveedorDTO;
import com.empresa.gestionproveedores.exception.ServiceException;
import com.empresa.gestionproveedores.service.ProveedorService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.Map;

/**
 * Managed Bean para el Dynamic Dialog de Proveedores
 */
@Named("proveedorDialogView")
@ViewScoped
@Slf4j
public class ProveedorDialogView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProveedorService proveedorService;

    @Getter
    @Setter
    private ProveedorDTO proveedor;

    @Getter
    @Setter
    private boolean modoEdicion;

    /**
     * Inicialización del bean
     * Recibe los parámetros del dialog
     */
    @PostConstruct
    public void init() {
        // Obtener parámetros de la sesión
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap();

        // Verificar si es modo edición
        ProveedorDTO proveedorEditar = (ProveedorDTO) sessionMap.get("proveedorEditar");

        if (proveedorEditar != null) {
            // Modo EDICIÓN: Copiar datos del proveedor existente
            proveedor = new ProveedorDTO();
            proveedor.setId(proveedorEditar.getId());
            proveedor.setRuc(proveedorEditar.getRuc());
            proveedor.setRazonSocial(proveedorEditar.getRazonSocial());
            proveedor.setNombreComercial(proveedorEditar.getNombreComercial());
            proveedor.setDireccion(proveedorEditar.getDireccion());
            proveedor.setTelefono(proveedorEditar.getTelefono());
            proveedor.setEmail(proveedorEditar.getEmail());
            proveedor.setContacto(proveedorEditar.getContacto());
            proveedor.setActivo(proveedorEditar.getActivo());

            modoEdicion = true;
            log.info("Dialog abierto en modo EDICIÓN para proveedor ID: {}", proveedor.getId());
        } else {
            // Modo CREACIÓN: Nuevo proveedor vacío
            proveedor = new ProveedorDTO();
            proveedor.setActivo(true); // Por defecto activo
            modoEdicion = false;
            log.info("Dialog abierto en modo CREACIÓN");
        }
    }

    /**
     * Guarda el proveedor (crear o actualizar)
     */
    public void guardar() {
        try {
            if (validarProveedor()) {
                if (modoEdicion) {
                    actualizar();
                } else {
                    crear();
                }
            }
        } catch (Exception e) {
            log.error("Error al guardar proveedor", e);
            mostrarMensajeError("Error al guardar el proveedor: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo proveedor
     */
    private void crear() {
        try {
            ProveedorDTO creado = proveedorService.crear(proveedor);
            log.info("Proveedor creado exitosamente: {}", creado.getRazonSocial());

            // Cerrar el dialog y retornar el proveedor creado
            PrimeFaces.current().dialog().closeDynamic(creado);

        } catch (ServiceException e) {
            log.error("Error al crear proveedor", e);
            mostrarMensajeError("Error al crear el proveedor: " + e.getMessage());
        }
    }

    /**
     * Actualiza un proveedor existente
     */
    private void actualizar() {
        try {
            ProveedorDTO actualizado = proveedorService.actualizar(proveedor.getId(), proveedor);
            log.info("Proveedor actualizado exitosamente: {}", actualizado.getRazonSocial());

            // Cerrar el dialog y retornar el proveedor actualizado
            PrimeFaces.current().dialog().closeDynamic(actualizado);

        } catch (ServiceException e) {
            log.error("Error al actualizar proveedor", e);
            mostrarMensajeError("Error al actualizar el proveedor: " + e.getMessage());
        }
    }

    /**
     * Cancela la operación y cierra el dialog
     */
    public void cancelar() {
        log.info("Operación cancelada por el usuario");
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    /**
     * Valida los datos del proveedor
     */
    private boolean validarProveedor() {
        if (proveedor.getRuc() == null || proveedor.getRuc().trim().isEmpty()) {
            mostrarMensajeAdvertencia("El RUC es obligatorio");
            return false;
        }

        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
            mostrarMensajeAdvertencia("La razón social es obligatoria");
            return false;
        }

        if (proveedor.getRuc().length() > 13) {
            mostrarMensajeAdvertencia("El RUC no puede tener más de 13 caracteres");
            return false;
        }

        return true;
    }

    // Métodos para mostrar mensajes

    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    private void mostrarMensajeAdvertencia(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensaje));
    }
}