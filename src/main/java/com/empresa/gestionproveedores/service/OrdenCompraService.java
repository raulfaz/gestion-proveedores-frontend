package com.empresa.gestionproveedores.service;

import com.empresa.gestionproveedores.config.ApiConfig;
import com.empresa.gestionproveedores.dto.ApiResponseDTO;
import com.empresa.gestionproveedores.dto.OrdenCompraDTO;
import com.empresa.gestionproveedores.exception.ServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class OrdenCompraService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ApiConfig apiConfig;

    @Inject
    private CloseableHttpClient httpClient;

    @Inject
    private ObjectMapper objectMapper;

    private String base() {
        // Asegúrate que ApiConfig exponga getBaseUrl()
        return apiConfig.getBaseUrl() + "/ordenes-compra";
    }

    public List<OrdenCompraDTO> listarTodas() {
        try {
            HttpGet request = new HttpGet(base());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<List<OrdenCompraDTO>> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<List<OrdenCompraDTO>>>() {});
                return api.getData() != null ? api.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar órdenes", e);
            throw new ServiceException("Error al obtener la lista de órdenes de compra", e);
        }
    }

    public List<OrdenCompraDTO> listarPorEstado(String estado) {
        try {
            HttpGet request = new HttpGet(base() + "/estado/" + estado);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<List<OrdenCompraDTO>> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<List<OrdenCompraDTO>>>() {});
                return api.getData() != null ? api.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar por estado", e);
            throw new ServiceException("Error al obtener órdenes por estado", e);
        }
    }

    public List<OrdenCompraDTO> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        try {
            String url = String.format("%s/fechas?inicio=%s&fin=%s", base(), inicio, fin);
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<List<OrdenCompraDTO>> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<List<OrdenCompraDTO>>>() {});
                return api.getData() != null ? api.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar por fechas", e);
            throw new ServiceException("Error al obtener órdenes por fechas", e);
        }
    }

    public List<OrdenCompraDTO> listarPorProveedor(Long proveedorId) {
        try {
            HttpGet request = new HttpGet(base() + "/proveedor/" + proveedorId);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<List<OrdenCompraDTO>> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<List<OrdenCompraDTO>>>() {});
                return api.getData() != null ? api.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar por proveedor", e);
            throw new ServiceException("Error al obtener órdenes por proveedor", e);
        }
    }

    public OrdenCompraDTO buscarPorId(Long id) {
        try {
            HttpGet request = new HttpGet(base() + "/" + id);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<OrdenCompraDTO> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<OrdenCompraDTO>>() {});
                return api.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar orden {}", id, e);
            throw new ServiceException("Error al buscar la orden de compra", e);
        }
    }

    public OrdenCompraDTO crear(OrdenCompraDTO dto) {
        try {
            HttpPost request = new HttpPost(base());
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(dto), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<OrdenCompraDTO> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<OrdenCompraDTO>>() {});
                return api.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al crear orden", e);
            throw new ServiceException("Error al crear la orden de compra", e);
        }
    }

    public OrdenCompraDTO actualizar(Long id, OrdenCompraDTO dto) {
        try {
            HttpPut request = new HttpPut(base() + "/" + id);
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(dto), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<OrdenCompraDTO> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<OrdenCompraDTO>>() {});
                return api.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al actualizar orden {}", id, e);
            throw new ServiceException("Error al actualizar la orden de compra", e);
        }
    }

    public void eliminar(Long id) {
        try {
            HttpDelete request = new HttpDelete(base() + "/" + id);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            log.error("Error al eliminar orden {}", id, e);
            throw new ServiceException("Error al eliminar la orden de compra", e);
        }
    }

    public void cambiarEstado(Long id, String nuevoEstado) {
        try {
            HttpPatch request = new HttpPatch(base() + "/" + id + "/estado?estado=" + nuevoEstado);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            log.error("Error al cambiar estado de orden {}", id, e);
            throw new ServiceException("Error al cambiar el estado de la orden", e);
        }
    }

    public String generarNumeroOrden() {
        try {
            HttpGet request = new HttpGet(base() + "/generar-numero");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ApiResponseDTO<String> api = objectMapper.readValue(
                        json, new TypeReference<ApiResponseDTO<String>>() {});
                return api.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al generar número de orden", e);
            throw new ServiceException("Error al generar número de orden", e);
        }
    }
}