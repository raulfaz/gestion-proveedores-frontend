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
import java.util.Map;

/**
 * Managed Bean para el Dynamic Dialog de Productos
 */
@Named("productoDialogView")
@ViewScoped
@Slf4j
public class ProductoDialogView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoService productoService;

    @Inject
    private ProveedorService proveedorService;

    @Getter
    @Setter
    private ProductoDTO producto;

    @Getter
    @Setter
    private boolean modoEdicion;

    @Getter
    @Setter
    private List<ProveedorDTO> proveedoresDisponibles;

    @PostConstruct
    public void init() {
        // Cargar proveedores activos
        cargarProveedores();

        // Obtener parámetros de la sesión
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap();

        // Verificar si es modo edición
        ProductoDTO productoEditar = (ProductoDTO) sessionMap.get("productoEditar");

        if (productoEditar != null) {
            // Modo EDICIÓN
            producto = new ProductoDTO();
            producto.setId(productoEditar.getId());
            producto.setCodigo(productoEditar.getCodigo());
            producto.setNombre(productoEditar.getNombre());
            producto.setDescripcion(productoEditar.getDescripcion());
            producto.setUnidadMedida(productoEditar.getUnidadMedida());
            producto.setPrecio(productoEditar.getPrecio());
            producto.setProveedorId(productoEditar.getProveedorId());
            producto.setActivo(productoEditar.getActivo());

            modoEdicion = true;
            log.info("Dialog abierto en modo EDICIÓN para producto ID: {}", producto.getId());
        } else {
            // Modo CREACIÓN
            producto = new ProductoDTO();
            producto.setActivo(true);
            producto.setPrecio(BigDecimal.ZERO);
            modoEdicion = false;
            log.info("Dialog abierto en modo CREACIÓN");
        }
    }

    private void cargarProveedores() {
        try {
            proveedoresDisponibles = proveedorService.listarActivos();
        } catch (Exception e) {
            log.error("Error al cargar proveedores", e);
            proveedoresDisponibles = new ArrayList<>();
            mostrarMensajeError("Error al cargar proveedores");
        }
    }

    /**
     * Guarda el producto (crear o actualizar)
     */
    public void guardar() {
        try {
            if (validarProducto()) {
                if (modoEdicion) {
                    actualizar();
                } else {
                    crear();
                }
            }
        } catch (Exception e) {
            log.error("Error al guardar producto", e);
            mostrarMensajeError("Error al guardar el producto: " + e.getMessage());
        }
    }

    private void crear() {
        try {
            ProductoDTO creado = productoService.crear(producto);
            log.info("Producto creado exitosamente: {}", creado.getNombre());

            PrimeFaces.current().dialog().closeDynamic(creado);

        } catch (ServiceException e) {
            log.error("Error al crear producto", e);
            mostrarMensajeError("Error al crear el producto: " + e.getMessage());
        }
    }

    private void actualizar() {
        try {
            ProductoDTO actualizado = productoService.actualizar(producto.getId(), producto);
            log.info("Producto actualizado exitosamente: {}", actualizado.getNombre());

            PrimeFaces.current().dialog().closeDynamic(actualizado);

        } catch (ServiceException e) {
            log.error("Error al actualizar producto", e);
            mostrarMensajeError("Error al actualizar el producto: " + e.getMessage());
        }
    }

    public void cancelar() {
        log.info("Operación cancelada por el usuario");
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    private boolean validarProducto() {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            mostrarMensajeAdvertencia("El código es obligatorio");
            return false;
        }

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            mostrarMensajeAdvertencia("El nombre es obligatorio");
            return false;
        }

        if (producto.getUnidadMedida() == null || producto.getUnidadMedida().trim().isEmpty()) {
            mostrarMensajeAdvertencia("La unidad de medida es obligatoria");
            return false;
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            mostrarMensajeAdvertencia("El precio debe ser mayor a 0");
            return false;
        }

        if (producto.getProveedorId() == null) {
            mostrarMensajeAdvertencia("Debe seleccionar un proveedor");
            return false;
        }

        return true;
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