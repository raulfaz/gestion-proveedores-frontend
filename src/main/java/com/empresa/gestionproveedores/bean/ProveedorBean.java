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
import java.util.Arrays;
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
    private List<String> funcionalidades;

    @PostConstruct
    public void init() {
        try {
            cargarProveedores();
            funcionalidades = Arrays.asList(
                    "Gestión completa de proveedores (CRUD)",
                    "Búsqueda y filtros avanzados",
                    "Activación/Desactivación de proveedores",
                    "Validaciones en tiempo real"
            );
            log.info("ProveedorBean inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar ProveedorBean", e);
            mostrarMensajeError("Error al cargar datos iniciales");
        }
    }

    /**
     * Abre el Dynamic Dialog para CREAR un nuevo proveedor
     */
    public void abrirDialogNuevo() {
        try {
            log.info("Intentando abrir dialog para nuevo proveedor");

            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("width", 700);
            options.put("height", 600);
            options.put("resizable", false);
            options.put("contentWidth", "100%");
            options.put("contentHeight", "100%");

            Map<String, List<String>> params = new HashMap<>();

            PrimeFaces.current().dialog().openDynamic(
                    "proveedorDialog",
                    options,
                    params
            );

            log.info("Dialog abierto exitosamente para crear nuevo proveedor");
        } catch (Exception e) {
            log.error("Error al abrir dialog", e);
            mostrarMensajeError("Error al abrir el formulario: " + e.getMessage());
        }
    }

    /**
     * Abre el Dynamic Dialog para EDITAR un proveedor existente
     */
    public void abrirDialogEditar(ProveedorDTO proveedor) {
        try {
            log.info("Intentando abrir dialog para editar proveedor ID: {}", proveedor.getId());

            // Guardar en sesión
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("proveedorEditar", proveedor);

            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("width", 700);
            options.put("height", 600);
            options.put("resizable", false);
            options.put("contentWidth", "100%");
            options.put("contentHeight", "100%");

            Map<String, List<String>> params = new HashMap<>();
            params.put("modoEdicion", Arrays.asList("true"));

            PrimeFaces.current().dialog().openDynamic(
                    "proveedorDialog",
                    options,
                    params
            );

            log.info("Dialog abierto exitosamente para editar proveedor ID: {}", proveedor.getId());
        } catch (Exception e) {
            log.error("Error al abrir dialog de edición", e);
            mostrarMensajeError("Error al abrir el formulario: " + e.getMessage());
        }
    }

    /**
     * Callback cuando se cierra el dialog
     */
    public void onDialogReturn(org.primefaces.event.SelectEvent<?> event) {
        try {
            Object obj = event.getObject();
            log.info("Dialog retornó: {}", obj);

            if (obj instanceof ProveedorDTO) {
                ProveedorDTO proveedor = (ProveedorDTO) obj;
                cargarProveedores();
                mostrarMensajeExito("Proveedor guardado: " + proveedor.getRazonSocial());
            } else {
                log.info("Dialog cerrado sin guardar");
            }
        } catch (Exception e) {
            log.error("Error en callback del dialog", e);
        } finally {
            // Limpiar sesión
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .remove("proveedorEditar");
        }
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

    private void mostrarMensajeExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }
}