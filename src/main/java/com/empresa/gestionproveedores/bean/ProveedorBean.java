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
import java.util.List;

@Named("proveedorBean")
@ViewScoped
@Slf4j
public class ProveedorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProveedorService proveedorService;

    @Getter @Setter
    private List<ProveedorDTO> proveedores;

    @Getter @Setter
    private ProveedorDTO proveedorSeleccionado;

    @Getter @Setter
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
                    "Activación/Desactivación",
                    "Validaciones básicas"
            );
            // Inicializar el seleccionado para evitar null bindings en el diálogo
            if (proveedorSeleccionado == null) {
                proveedorSeleccionado = new ProveedorDTO();
                proveedorSeleccionado.setActivo(true);
            }
            log.info("ProveedorBean inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar ProveedorBean", e);
            mostrarMensajeError("Error al cargar datos iniciales");
            proveedores = new ArrayList<>();
        }
    }

    public void prepararNuevo() {
        proveedorSeleccionado = new ProveedorDTO();
        proveedorSeleccionado.setActivo(true);
    }

    public void prepararEditar(ProveedorDTO proveedor) {
        if (proveedor == null) {
            mostrarMensajeAdvertencia("Proveedor inválido");
            return;
        }
        // Clonar para evitar modificar directamente el objeto de la tabla
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
    }

    public void guardar() {
        try {
            if (!validarProveedor()) return;

            if (proveedorSeleccionado.getId() == null) {
                proveedorService.crear(proveedorSeleccionado);
                mostrarMensajeExito("Proveedor creado exitosamente");
            } else {
                proveedorService.actualizar(proveedorSeleccionado.getId(), proveedorSeleccionado);
                mostrarMensajeExito("Proveedor actualizado exitosamente");
            }
            cargarProveedores();
            PrimeFaces.current().executeScript("PF('dlgProveedor').hide();");
        } catch (ServiceException e) {
            log.error("Error al guardar proveedor", e);
            mostrarMensajeError("Error al guardar el proveedor: " + e.getMessage());
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
            boolean nuevoEstado = !Boolean.TRUE.equals(proveedor.getActivo());
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
            log.error("Error al buscar proveedores", e);
            mostrarMensajeError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void limpiarBusqueda() {
        criterioBusqueda = "";
        cargarProveedores();
    }

    public void cargarProveedores() {
        try {
            proveedores = proveedorService.listarTodos();
        } catch (ServiceException e) {
            log.error("Error al cargar proveedores", e);
            proveedores = new ArrayList<>();
            mostrarMensajeError("Error al cargar proveedores: " + e.getMessage());
        }
    }

    public int getTotalProveedores() {
        return proveedores != null ? proveedores.size() : 0;
    }

    public long getTotalProveedoresActivos() {
        return proveedores != null
                ? proveedores.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count()
                : 0;
    }

    public String getTituloDialogo() {
        return (proveedorSeleccionado != null && proveedorSeleccionado.getId() != null)
                ? "Editar Proveedor"
                : "Nuevo Proveedor";
    }

    private boolean validarProveedor() {
        if (proveedorSeleccionado.getRuc() == null || proveedorSeleccionado.getRuc().isBlank()) {
            mostrarMensajeAdvertencia("El RUC es obligatorio");
            return false;
        }
        if (proveedorSeleccionado.getRazonSocial() == null || proveedorSeleccionado.getRazonSocial().isBlank()) {
            mostrarMensajeAdvertencia("La razón social es obligatoria");
            return false;
        }
        if (proveedorSeleccionado.getRuc().length() > 13) {
            mostrarMensajeAdvertencia("El RUC no puede tener más de 13 caracteres");
            return false;
        }
        return true;
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