package com.empresa.gestionproveedores.bean;

import com.empresa.gestionproveedores.dto.*;
import com.empresa.gestionproveedores.exception.ServiceException;
import com.empresa.gestionproveedores.service.OrdenCompraService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("ordenCompraBean")
@ViewScoped
@Slf4j
public class OrdenCompraBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.12");
    private static final DateTimeFormatter DF_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Inject private OrdenCompraService ordenCompraService;
    @Inject private ProveedorService proveedorService;
    @Inject private ProductoService productoService;

    @Getter @Setter
    private List<OrdenCompraDTO> ordenes;

    @Getter @Setter
    private OrdenCompraDTO ordenSeleccionada;

    @Getter @Setter
    private List<ProveedorDTO> proveedoresDisponibles;

    // Lista dinámica de productos según proveedor
    @Getter @Setter
    private List<ProductoDTO> productosDisponibles;

    // Filtros
    @Getter @Setter
    private String estadoFiltro;

    @Getter @Setter
    private LocalDate fechaInicio;

    @Getter @Setter
    private LocalDate fechaFin;

    // Campos para agregar detalle (solo cantidad y producto)
    @Getter @Setter
    private Long detalleProductoId;

    @Getter @Setter
    private Integer detalleCantidad;

    @PostConstruct
    public void init() {
        try {
            cargarOrdenes();
            proveedoresDisponibles = proveedorService.listarActivos();
            productosDisponibles = new ArrayList<>(); // Vacía hasta elegir proveedor
            if (ordenSeleccionada == null) {
                ordenSeleccionada = nuevaOrdenPorDefecto(true);
            }
        } catch (Exception e) {
            log.error("Error al inicializar OrdenCompraBean", e);
            ordenes = new ArrayList<>();
            proveedoresDisponibles = new ArrayList<>();
            productosDisponibles = new ArrayList<>();
            mostrarMensajeError("Error al cargar datos iniciales");
        }
    }

    private OrdenCompraDTO nuevaOrdenPorDefecto(boolean generarNumero) {
        OrdenCompraDTO o = new OrdenCompraDTO();
        o.setEstado("PENDIENTE");
        o.setFechaOrden(LocalDate.now());
        o.setDetalles(new ArrayList<>());
        o.setSubtotal(BigDecimal.ZERO);
        o.setIva(BigDecimal.ZERO);
        o.setTotal(BigDecimal.ZERO);
        if (generarNumero) {
            try {
                o.setNumeroOrden(ordenCompraService.generarNumeroOrden());
            } catch (ServiceException ex) {
                log.warn("No se pudo generar número automático", ex);
            }
        }
        return o;
    }

    public void prepararNuevo() {
        ordenSeleccionada = nuevaOrdenPorDefecto(true);
        productosDisponibles = new ArrayList<>();
        detalleProductoId = null;
        detalleCantidad = null;
    }

    public void prepararEditar(OrdenCompraDTO o) {
        if (o == null) {
            mostrarMensajeAdvertencia("Orden inválida");
            return;
        }
        OrdenCompraDTO completa = ordenCompraService.buscarPorId(o.getId());
        ordenSeleccionada = (completa != null) ? completa : o;
        if (ordenSeleccionada.getDetalles() == null) {
            ordenSeleccionada.setDetalles(new ArrayList<>());
        }
        // Cargar productos del proveedor actual (si tiene proveedor asignado)
        if (ordenSeleccionada.getProveedorId() != null) {
            productosDisponibles = productoService.buscarPorProveedor(ordenSeleccionada.getProveedorId());
        } else {
            productosDisponibles = new ArrayList<>();
        }
        recalcularTotales();
    }

    public void guardar() {
        try {
            if (!validarOrden()) return;

            if (ordenSeleccionada.getId() == null) {
                ordenSeleccionada = ordenCompraService.crear(ordenSeleccionada);
                mostrarMensajeExito("Orden creada exitosamente");
            } else {
                ordenSeleccionada = ordenCompraService.actualizar(ordenSeleccionada.getId(), ordenSeleccionada);
                mostrarMensajeExito("Orden actualizada exitosamente");
            }
            cargarOrdenes();
            PrimeFaces.current().executeScript("PF('dlgOrden').hide();");
        } catch (ServiceException e) {
            log.error("Error al guardar orden", e);
            mostrarMensajeError("Error al guardar la orden: " + e.getMessage());
        }
    }

    public void eliminar(OrdenCompraDTO o) {
        try {
            ordenCompraService.eliminar(o.getId());
            cargarOrdenes();
            mostrarMensajeExito("Orden eliminada exitosamente");
        } catch (ServiceException e) {
            log.error("Error al eliminar orden", e);
            mostrarMensajeError("Error al eliminar la orden: " + e.getMessage());
        }
    }

    public void cambiarEstado(OrdenCompraDTO o, String nuevoEstado) {
        try {
            ordenCompraService.cambiarEstado(o.getId(), nuevoEstado);
            o.setEstado(nuevoEstado);
            mostrarMensajeExito("Estado cambiado a " + nuevoEstado);
        } catch (ServiceException e) {
            log.error("Error al cambiar estado", e);
            mostrarMensajeError("Error al cambiar estado: " + e.getMessage());
        }
    }

    public void buscar() {
        try {
            if (estadoFiltro != null && !estadoFiltro.isBlank()) {
                ordenes = ordenCompraService.listarPorEstado(estadoFiltro);
            } else if (fechaInicio != null && fechaFin != null) {
                ordenes = ordenCompraService.listarPorRangoFechas(fechaInicio, fechaFin);
            } else {
                cargarOrdenes();
            }
        } catch (ServiceException e) {
            log.error("Error en búsqueda", e);
            mostrarMensajeError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void limpiarFiltros() {
        estadoFiltro = null;
        fechaInicio = null;
        fechaFin = null;
        cargarOrdenes();
    }

    public void cargarOrdenes() {
        try {
            ordenes = ordenCompraService.listarTodas();
        } catch (ServiceException e) {
            log.error("Error al cargar órdenes", e);
            ordenes = new ArrayList<>();
            mostrarMensajeError("Error al cargar órdenes: " + e.getMessage());
        }
    }

    // ----------------- Dinámica de productos según proveedor -----------------
    public void onProveedorChange() {
        if (ordenSeleccionada.getProveedorId() == null) {
            productosDisponibles = new ArrayList<>();
            ordenSeleccionada.getDetalles().clear();
            detalleProductoId = null;
            detalleCantidad = null;
            recalcularTotales();
            return;
        }
        try {
            productosDisponibles = productoService.buscarPorProveedor(ordenSeleccionada.getProveedorId());
            detalleProductoId = null;
            detalleCantidad = null;
        } catch (ServiceException e) {
            log.error("Error al cargar productos del proveedor", e);
            productosDisponibles = new ArrayList<>();
            mostrarMensajeError("Error al cargar productos del proveedor");
        }
    }

    // ----------------- Detalles -----------------
    public void agregarDetalle() {
        if (detalleProductoId == null) {
            mostrarMensajeAdvertencia("Seleccione un producto"); return;
        }
        if (detalleCantidad == null || detalleCantidad < 1) {
            mostrarMensajeAdvertencia("La cantidad debe ser mayor a 0"); return;
        }

        ProductoDTO prod = productosDisponibles.stream()
                .filter(p -> p.getId().equals(detalleProductoId))
                .findFirst()
                .orElse(null);

        if (prod == null) {
            mostrarMensajeAdvertencia("Producto no encontrado");
            return;
        }

        BigDecimal precioUnitario = prod.getPrecio() != null ? prod.getPrecio() : BigDecimal.ZERO;

        DetalleOrdenDTO det = new DetalleOrdenDTO();
        det.setProductoId(detalleProductoId);
        det.setCantidad(detalleCantidad);
        det.setPrecioUnitario(precioUnitario);
        det.setSubtotal(precioUnitario.multiply(new BigDecimal(detalleCantidad)));

        det.setProducto(ProductoResponseDTO.builder()
                .id(prod.getId())
                .codigo(prod.getCodigo())
                .nombre(prod.getNombre())
                .unidadMedida(prod.getUnidadMedida())
                .precio(prod.getPrecio())
                .activo(prod.getActivo())
                .build());

        ordenSeleccionada.getDetalles().add(det);

        detalleProductoId = null;
        detalleCantidad = null;
        recalcularTotales();
    }

    public void eliminarDetalle(DetalleOrdenDTO det) {
        ordenSeleccionada.getDetalles().remove(det);
        recalcularTotales();
    }

    private void recalcularTotales() {
        BigDecimal sub = ordenSeleccionada.getDetalles().stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal iva = sub.multiply(IVA_PORCENTAJE).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal total = sub.add(iva);

        ordenSeleccionada.setSubtotal(sub);
        ordenSeleccionada.setIva(iva);
        ordenSeleccionada.setTotal(total);
    }

    private boolean validarOrden() {
        if (ordenSeleccionada.getProveedorId() == null) {
            mostrarMensajeAdvertencia("Seleccione un proveedor"); return false;
        }
        if (ordenSeleccionada.getFechaOrden() == null) {
            mostrarMensajeAdvertencia("Seleccione la fecha de la orden"); return false;
        }
        if (ordenSeleccionada.getDetalles() == null || ordenSeleccionada.getDetalles().isEmpty()) {
            mostrarMensajeAdvertencia("Agregue al menos un detalle"); return false;
        }
        return true;
    }

    public String getTituloDialogo() {
        return (ordenSeleccionada != null && ordenSeleccionada.getId() != null)
                ? "Editar Orden de Compra" : "Nueva Orden de Compra";
    }

    public String formatFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(DF_DDMMYYYY) : "";
    }

    // Mensajes
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