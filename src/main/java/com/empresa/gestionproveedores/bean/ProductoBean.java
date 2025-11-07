package com.empresa.gestionproveedores.bean;

import com.empresa.gestionproveedores.dto.ProductoDTO;
import com.empresa.gestionproveedores.dto.ProveedorDTO;
import com.empresa.gestionproveedores.exception.ServiceException;
import com.empresa.gestionproveedores.service.ProductoService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Named("productoBean")
@ViewScoped
@Slf4j
public class ProductoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoService productoService;

    @Inject
    private ProveedorService proveedorService;

    @Getter @Setter
    private List<ProductoDTO> productos;

    @Getter @Setter
    private ProductoDTO productoSeleccionado;

    @Getter @Setter
    private List<ProveedorDTO> proveedoresDisponibles;

    @Getter @Setter
    private String criterioBusqueda;

    @PostConstruct
    public void init() {
        try {
            cargarProductos();
            proveedoresDisponibles = proveedorService.listarActivos();
            if (productoSeleccionado == null) {
                productoSeleccionado = new ProductoDTO();
                productoSeleccionado.setActivo(true);
                productoSeleccionado.setPrecio(BigDecimal.ZERO);
            }
            log.info("ProductoBean inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar ProductoBean", e);
            productos = new ArrayList<>();
            proveedoresDisponibles = new ArrayList<>();
            mostrarMensajeError("Error al cargar datos iniciales");
        }
    }

    public void prepararNuevo() {
        productoSeleccionado = new ProductoDTO();
        productoSeleccionado.setActivo(true);
        productoSeleccionado.setPrecio(BigDecimal.ZERO);
    }

    public void prepararEditar(ProductoDTO p) {
        if (p == null) {
            mostrarMensajeAdvertencia("Producto inválido");
            return;
        }
        productoSeleccionado = new ProductoDTO();
        productoSeleccionado.setId(p.getId());
        productoSeleccionado.setCodigo(p.getCodigo());
        productoSeleccionado.setNombre(p.getNombre());
        productoSeleccionado.setDescripcion(p.getDescripcion());
        productoSeleccionado.setUnidadMedida(p.getUnidadMedida());
        productoSeleccionado.setPrecio(p.getPrecio());
        productoSeleccionado.setProveedorId(p.getProveedorId());
        productoSeleccionado.setActivo(p.getActivo());
    }

    public void guardar() {
        try {
            if (!validarProducto()) return;

            if (productoSeleccionado.getId() == null) {
                productoService.crear(productoSeleccionado);
                mostrarMensajeExito("Producto creado exitosamente");
            } else {
                productoService.actualizar(productoSeleccionado.getId(), productoSeleccionado);
                mostrarMensajeExito("Producto actualizado exitosamente");
            }
            cargarProductos();
            PrimeFaces.current().executeScript("PF('dlgProducto').hide();");
        } catch (ServiceException e) {
            log.error("Error al guardar producto", e);
            mostrarMensajeError("Error al guardar el producto: " + e.getMessage());
        }
    }

    public void eliminar(ProductoDTO producto) {
        try {
            productoService.eliminar(producto.getId());
            cargarProductos();
            mostrarMensajeExito("Producto eliminado exitosamente");
        } catch (ServiceException e) {
            log.error("Error al eliminar producto", e);
            mostrarMensajeError("Error al eliminar: " + e.getMessage());
        }
    }

    public void cambiarEstado(ProductoDTO producto) {
        try {
            boolean nuevoEstado = !Boolean.TRUE.equals(producto.getActivo());
            productoService.cambiarEstado(producto.getId(), nuevoEstado);
            producto.setActivo(nuevoEstado);
            mostrarMensajeExito(nuevoEstado ? "Producto activado" : "Producto desactivado");
        } catch (ServiceException e) {
            log.error("Error al cambiar estado", e);
            mostrarMensajeError("Error: " + e.getMessage());
        }
    }

    public void buscar() {
        try {
            if (criterioBusqueda != null && !criterioBusqueda.trim().isEmpty()) {
                productos = productoService.buscarPorNombre(criterioBusqueda.trim());
            } else {
                cargarProductos();
            }
        } catch (ServiceException e) {
            log.error("Error al buscar productos", e);
            mostrarMensajeError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void limpiarBusqueda() {
        criterioBusqueda = "";
        cargarProductos();
    }

    public void cargarProductos() {
        try {
            productos = productoService.listarTodos();
        } catch (ServiceException e) {
            log.error("Error al cargar productos", e);
            productos = new ArrayList<>();
            mostrarMensajeError("Error al cargar productos: " + e.getMessage());
        }
    }

    public int getTotalProductos() {
        return productos != null ? productos.size() : 0;
    }

    public long getTotalProductosActivos() {
        return productos != null
                ? productos.stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).count()
                : 0;
    }

    public String getTituloDialogo() {
        return (productoSeleccionado != null && productoSeleccionado.getId() != null)
                ? "Editar Producto"
                : "Nuevo Producto";
    }

    private boolean validarProducto() {
        if (productoSeleccionado.getCodigo() == null || productoSeleccionado.getCodigo().isBlank()) {
            mostrarMensajeAdvertencia("El código es obligatorio"); return false;
        }
        if (productoSeleccionado.getNombre() == null || productoSeleccionado.getNombre().isBlank()) {
            mostrarMensajeAdvertencia("El nombre es obligatorio"); return false;
        }
        if (productoSeleccionado.getUnidadMedida() == null || productoSeleccionado.getUnidadMedida().isBlank()) {
            mostrarMensajeAdvertencia("La unidad de medida es obligatoria"); return false;
        }
        if (productoSeleccionado.getPrecio() == null ||
                productoSeleccionado.getPrecio().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            mostrarMensajeAdvertencia("El precio debe ser mayor a 0"); return false;
        }
        if (productoSeleccionado.getProveedorId() == null) {
            mostrarMensajeAdvertencia("Debe seleccionar un proveedor"); return false;
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