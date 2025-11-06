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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Managed Bean para gestión de Proveedores
 */
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
    private List<ProveedorDTO> proveedoresFiltrados;

    @Getter
    @Setter
    private ProveedorDTO proveedorSeleccionado;

    @Getter
    @Setter
    private ProveedorDTO nuevoProveedor;

    @Getter
    @Setter
    private boolean modoEdicion;

    @Getter
    @Setter
    private String criterioBusqueda;

    // Lista de funcionalidades
    @Getter
    private List<String> funcionalidades;

    /**
     * Inicialización del bean
     */
    @PostConstruct
    public void init() {
        try {
            cargarProveedores();
            nuevoProveedor = new ProveedorDTO();
            nuevoProveedor.setActivo(true);
            modoEdicion = false;

            // Inicializar lista de funcionalidades
            funcionalidades = Arrays.asList(
                    "Gestión completa de proveedores (CRUD)",
                    "Búsqueda y filtros avanzados",
                    "Activación/Desactivación de proveedores",
                    "Validaciones en tiempo real",
                    "Gestión de productos (Próximamente)",
                    "Órdenes de compra (Próximamente)",
                    "Reportes en PDF (Próximamente)"
            );
        } catch (Exception e) {
            log.error("Error al inicializar ProveedorBean", e);
            mostrarMensajeError("Error al cargar datos iniciales");
        }
    }

    /**
     * Carga todos los proveedores
     */
    public void cargarProveedores() {
        try {
            proveedores = proveedorService.listarTodos();
            log.info("Proveedores cargados: {}", proveedores.size());
        } catch (ServiceException e) {
            log.error("Error al cargar proveedores", e);
            proveedores = new ArrayList<>();
            mostrarMensajeError("Error al cargar la lista de proveedores: " + e.getMessage());
        }
    }

    /**
     * Carga solo proveedores activos
     */
    public void cargarProveedoresActivos() {
        try {
            proveedores = proveedorService.listarActivos();
            log.info("Proveedores activos cargados: {}", proveedores.size());
        } catch (ServiceException e) {
            log.error("Error al cargar proveedores activos", e);
            proveedores = new ArrayList<>();
            mostrarMensajeError("Error al cargar proveedores activos: " + e.getMessage());
        }
    }

    /**
     * Prepara un nuevo proveedor para crear
     */
    public void prepararNuevo() {
        nuevoProveedor = new ProveedorDTO();
        nuevoProveedor.setActivo(true);
        modoEdicion = false;
        log.info("Preparando nuevo proveedor");
    }

    /**
     * Prepara un proveedor para editar
     */
    public void prepararEdicion(ProveedorDTO proveedor) {
        nuevoProveedor = new ProveedorDTO();
        nuevoProveedor.setId(proveedor.getId());
        nuevoProveedor.setRuc(proveedor.getRuc());
        nuevoProveedor.setRazonSocial(proveedor.getRazonSocial());
        nuevoProveedor.setNombreComercial(proveedor.getNombreComercial());
        nuevoProveedor.setDireccion(proveedor.getDireccion());
        nuevoProveedor.setTelefono(proveedor.getTelefono());
        nuevoProveedor.setEmail(proveedor.getEmail());
        nuevoProveedor.setContacto(proveedor.getContacto());
        nuevoProveedor.setActivo(proveedor.getActivo());

        modoEdicion = true;
        log.info("Preparando edición del proveedor ID: {}", proveedor.getId());
    }

    /**
     * Guarda un proveedor (crear o actualizar)
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
            ProveedorDTO creado = proveedorService.crear(nuevoProveedor);
            cargarProveedores();
            mostrarMensajeExito("Proveedor creado exitosamente");
            prepararNuevo();
            log.info("Proveedor creado: {}", creado.getRazonSocial());
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
            ProveedorDTO actualizado = proveedorService.actualizar(nuevoProveedor.getId(), nuevoProveedor);
            cargarProveedores();
            mostrarMensajeExito("Proveedor actualizado exitosamente");
            prepararNuevo();
            log.info("Proveedor actualizado: {}", actualizado.getRazonSocial());
        } catch (ServiceException e) {
            log.error("Error al actualizar proveedor", e);
            mostrarMensajeError("Error al actualizar el proveedor: " + e.getMessage());
        }
    }

    /**
     * Elimina un proveedor
     */
    public void eliminar(ProveedorDTO proveedor) {
        try {
            proveedorService.eliminar(proveedor.getId());
            cargarProveedores();
            mostrarMensajeExito("Proveedor eliminado exitosamente");
            log.info("Proveedor eliminado ID: {}", proveedor.getId());
        } catch (ServiceException e) {
            log.error("Error al eliminar proveedor", e);
            mostrarMensajeError("Error al eliminar el proveedor: " + e.getMessage());
        }
    }

    /**
     * Cambia el estado de un proveedor (activo/inactivo)
     */
    public void cambiarEstado(ProveedorDTO proveedor) {
        try {
            boolean nuevoEstado = !proveedor.getActivo();
            proveedorService.cambiarEstado(proveedor.getId(), nuevoEstado);
            proveedor.setActivo(nuevoEstado);

            String mensaje = nuevoEstado ? "Proveedor activado" : "Proveedor desactivado";
            mostrarMensajeExito(mensaje);
            log.info("Estado cambiado para proveedor ID: {} a {}", proveedor.getId(), nuevoEstado);
        } catch (ServiceException e) {
            log.error("Error al cambiar estado del proveedor", e);
            mostrarMensajeError("Error al cambiar el estado: " + e.getMessage());
        }
    }

    /**
     * Busca proveedores por razón social
     */
    public void buscar() {
        try {
            if (criterioBusqueda != null && !criterioBusqueda.trim().isEmpty()) {
                proveedores = proveedorService.buscarPorRazonSocial(criterioBusqueda.trim());
                log.info("Búsqueda realizada: {} - Resultados: {}", criterioBusqueda, proveedores.size());
            } else {
                cargarProveedores();
            }
        } catch (ServiceException e) {
            log.error("Error al buscar proveedores", e);
            mostrarMensajeError("Error en la búsqueda: " + e.getMessage());
        }
    }

    /**
     * Limpia el formulario de búsqueda y recarga todos los proveedores
     */
    public void limpiarBusqueda() {
        criterioBusqueda = "";
        cargarProveedores();
    }

    /**
     * Cancela la operación actual
     */
    public void cancelar() {
        prepararNuevo();
        mostrarMensajeInfo("Operación cancelada");
    }

    /**
     * Valida los datos del proveedor
     */
    private boolean validarProveedor() {
        if (nuevoProveedor.getRuc() == null || nuevoProveedor.getRuc().trim().isEmpty()) {
            mostrarMensajeAdvertencia("El RUC es obligatorio");
            return false;
        }

        if (nuevoProveedor.getRazonSocial() == null || nuevoProveedor.getRazonSocial().trim().isEmpty()) {
            mostrarMensajeAdvertencia("La razón social es obligatoria");
            return false;
        }

        if (nuevoProveedor.getRuc().length() > 13) {
            mostrarMensajeAdvertencia("El RUC no puede tener más de 13 caracteres");
            return false;
        }

        return true;
    }

    /**
     * Obtiene el total de proveedores
     */
    public int getTotalProveedores() {
        return proveedores != null ? proveedores.size() : 0;
    }

    /**
     * Obtiene el total de proveedores activos
     */
    public long getTotalProveedoresActivos() {
        return proveedores != null ?
                proveedores.stream().filter(p -> p.getActivo() != null && p.getActivo()).count() : 0;
    }

    // Métodos para mostrar mensajes

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

    private void mostrarMensajeInfo(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", mensaje));
    }
}
