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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("proveedorBean")
@ViewScoped
@Slf4j
public class ProveedorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProveedorService proveedorService;

    @Getter
    @Setter
    private List<ProveedorDTO> proveedores;

    @Getter
    @Setter
    private ProveedorDTO proveedorSeleccionado;

    @Getter
    @Setter
    private String criterioBusqueda;

    @Getter
    @Setter
    private boolean dialogoVisible = false;

    @PostConstruct
    public void init() {
        try {
            cargarProveedores();
            log.info("ProveedorBean inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar ProveedorBean", e);
            mostrarMensajeError("Error al cargar datos iniciales");
        }
    }

    /**
     * Prepara para crear un NUEVO proveedor
     */
    public void prepararNuevo() {
        try {
            proveedorSeleccionado = new ProveedorDTO();
            proveedorSeleccionado.setActivo(true);
            dialogoVisible = true;
            log.info("Preparado para crear nuevo proveedor");
        } catch (Exception e) {
            log.error("Error al preparar nuevo proveedor", e);
            mostrarMensajeError("Error al abrir formulario");
        }
    }

    /**
     * Prepara para EDITAR un proveedor existente
     */
    public void prepararEditar(ProveedorDTO proveedor) {
        try {
            // Clonar el proveedor para edición
            proveedorSeleccionado = new ProveedorDTO();
            proveedorSeleccionado.setId(proveedor.getId());
            proveedorSeleccionado.setRuc(proveedor.getRuc());
            proveedorSeleccionado.setRazonSocial(proveedor.getRazonSocial());
            proveedorSeleccionado.setNombreComercial(proveedor.getNombreComercial());
            proveedorSeleccionado.setDireccion(proveedor.getDireccion());
            proveedorSeleccionado.setTelefono(proveedor.getTelefono());
            proveedorSeleccionado.setEmail(proveedor.getEmail());
            proveedorSeleccionado.setContacto(proveedor.getContacto());
            proveedorSeleccionado.setActivo(proveedor.getActivo());

            dialogoVisible = true;
            log.info("Preparado para editar proveedor ID: {}", proveedor.getId());
        } catch (Exception e) {
            log.error("Error al preparar edición", e);
            mostrarMensajeError("Error al abrir formulario de edición");
        }
    }

    /**
     * Guarda el proveedor (crear o actualizar)
     */
    public void guardar() {
        try {
            if (validarProveedor()) {
                if (proveedorSeleccionado.getId() != null) {
                    // Actualizar
                    proveedorService.actualizar(proveedorSeleccionado.getId(), proveedorSeleccionado);
                    mostrarMensajeExito("Proveedor actualizado exitosamente");
                    log.info("Proveedor actualizado: {}", proveedorSeleccionado.getRazonSocial());
                } else {
                    // Crear
                    proveedorService.crear(proveedorSeleccionado);
                    mostrarMensajeExito("Proveedor creado exitosamente");
                    log.info("Proveedor creado: {}", proveedorSeleccionado.getRazonSocial());
                }

                cerrarDialogo();
                cargarProveedores();
            }
        } catch (ServiceException e) {
            log.error("Error al guardar proveedor", e);
            mostrarMensajeError("Error al guardar: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar", e);
            mostrarMensajeError("Error inesperado al guardar");
        }
    }

    /**
     * Valida los datos del proveedor
     */
    private boolean validarProveedor() {
        if (proveedorSeleccionado.getRuc() == null || proveedorSeleccionado.getRuc().trim().isEmpty()) {
            mostrarMensajeAdvertencia("El RUC es obligatorio");
            return false;
        }

        if (proveedorSeleccionado.getRazonSocial() == null || proveedorSeleccionado.getRazonSocial().trim().isEmpty()) {
            mostrarMensajeAdvertencia("La razón social es obligatoria");
            return false;
        }

        if (proveedorSeleccionado.getRuc().length() > 13) {
            mostrarMensajeAdvertencia("El RUC no puede tener más de 13 caracteres");
            return false;
        }

        return true;
    }

    /**
     * Cierra el diálogo
     */
    public void cerrarDialogo() {
        dialogoVisible = false;
        proveedorSeleccionado = null;
        PrimeFaces.current().executeScript("PF('dlgProveedor').hide();");
    }

    public void cargarProveedores() {
        try {
            proveedores = proveedorService.listarTodos();
            log.info("Proveedores cargados: {}", proveedores.size());
        } catch (ServiceException e) {
            log.error("Error al cargar proveedores", e);
            proveedores = new ArrayList<>();
            mostrarMensajeError("Error al cargar proveedores: " + e.getMessage());
        }
    }

    public void cargarProveedoresActivos() {
        try {
            proveedores = proveedorService.listarActivos();
            log.info("Proveedores activos cargados: {}", proveedores.size());
        } catch (ServiceException e) {
            log.error("Error al cargar proveedores activos", e);
            proveedores = new ArrayList<>();
            mostrarMensajeError("Error: " + e.getMessage());
        }
    }

    public void eliminar(ProveedorDTO proveedor) {
        try {
            proveedorService.eliminar(proveedor.getId());
            cargarProveedores();
            mostrarMensajeExito("Proveedor eliminado exitosamente");
            log.info("Proveedor eliminado ID: {}", proveedor.getId());
        } catch (ServiceException e) {
            log.error("Error al eliminar proveedor", e);
            mostrarMensajeError("Error al eliminar: " + e.getMessage());
        }
    }

    public void cambiarEstado(ProveedorDTO proveedor) {
        try {
            boolean nuevoEstado = !proveedor.getActivo();
            proveedorService.cambiarEstado(proveedor.getId(), nuevoEstado);
            proveedor.setActivo(nuevoEstado);
            mostrarMensajeExito(nuevoEstado ? "Proveedor activado" : "Proveedor desactivado");
        } catch (ServiceException e) {
            log.error("Error al cambiar estado", e);
            mostrarMensajeError("Error: " + e.getMessage());
        }
    }

    public void buscar() {
        try {
            if (criterioBusqueda != null && !criterioBusqueda.trim().isEmpty()) {
                proveedores = proveedorService.buscarPorRazonSocial(criterioBusqueda.trim());
            } else {
                cargarProveedores();
            }
        } catch (ServiceException e) {
            log.error("Error al buscar", e);
            mostrarMensajeError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void limpiarBusqueda() {
        criterioBusqueda = "";
        cargarProveedores();
    }

    public int getTotalProveedores() {
        return proveedores != null ? proveedores.size() : 0;
    }

    public long getTotalProveedoresActivos() {
        return proveedores != null ?
                proveedores.stream().filter(p -> p.getActivo() != null && p.getActivo()).count() : 0;
    }

    public String getTituloDialogo() {
        return proveedorSeleccionado != null && proveedorSeleccionado.getId() != null
                ? "Editar Proveedor"
                : "Nuevo Proveedor";
    }

    private void mostrarMensajeExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    private void mostrarMensajeAdvertencia(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensaje));
    }
}