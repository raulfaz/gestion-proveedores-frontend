package com.empresa.gestionproveedores.service;

import com.empresa.gestionproveedores.config.ApiConfig;
import com.empresa.gestionproveedores.dto.ApiResponseDTO;
import com.empresa.gestionproveedores.dto.ProductoDTO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para consumir API REST de Productos
 */
@ApplicationScoped
@Slf4j
public class ProductoService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ApiConfig apiConfig;

    @Inject
    private CloseableHttpClient httpClient;

    @Inject
    private ObjectMapper objectMapper;

    /**
     * Lista todos los productos
     */
    public List<ProductoDTO> listarTodos() {
        try {
            String url = apiConfig.getBaseUrl() + "/productos";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProductoDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProductoDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar productos", e);
            throw new ServiceException("Error al obtener la lista de productos", e);
        }
    }

    /**
     * Lista productos activos
     */
    public List<ProductoDTO> listarActivos() {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/activos";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProductoDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProductoDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar productos activos", e);
            throw new ServiceException("Error al obtener productos activos", e);
        }
    }

    /**
     * Busca un producto por ID
     */
    public ProductoDTO buscarPorId(Long id) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/" + id;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProductoDTO> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<ProductoDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar producto por ID: {}", id, e);
            throw new ServiceException("Error al buscar el producto", e);
        }
    }

    /**
     * Crea un nuevo producto
     */
    public ProductoDTO crear(ProductoDTO productoDTO) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos";
            HttpPost request = new HttpPost(url);

            String jsonBody = objectMapper.writeValueAsString(productoDTO);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProductoDTO> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<ProductoDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al crear producto", e);
            throw new ServiceException("Error al crear el producto", e);
        }
    }

    /**
     * Actualiza un producto existente
     */
    public ProductoDTO actualizar(Long id, ProductoDTO productoDTO) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/" + id;
            HttpPut request = new HttpPut(url);

            String jsonBody = objectMapper.writeValueAsString(productoDTO);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProductoDTO> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<ProductoDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al actualizar producto ID: {}", id, e);
            throw new ServiceException("Error al actualizar el producto", e);
        }
    }

    /**
     * Elimina un producto
     */
    public void eliminar(Long id) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/" + id;
            HttpDelete request = new HttpDelete(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            log.error("Error al eliminar producto ID: {}", id, e);
            throw new ServiceException("Error al eliminar el producto", e);
        }
    }

    /**
     * Cambia el estado activo/inactivo de un producto
     */
    public void cambiarEstado(Long id, Boolean nuevoEstado) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/" + id + "/estado?activo=" + nuevoEstado;
            HttpPatch request = new HttpPatch(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            log.error("Error al cambiar estado del producto ID: {}", id, e);
            throw new ServiceException("Error al cambiar el estado del producto", e);
        }
    }

    /**
     * Busca productos por proveedor
     */
    public List<ProductoDTO> buscarPorProveedor(Long proveedorId) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/proveedor/" + proveedorId;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProductoDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProductoDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar productos por proveedor", e);
            throw new ServiceException("Error al buscar productos del proveedor", e);
        }
    }

    /**
     * Busca productos por nombre
     */
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        try {
            String url = apiConfig.getBaseUrl() + "/productos/nombre?nombre=" + nombre;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProductoDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProductoDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar productos por nombre", e);
            throw new ServiceException("Error en la búsqueda de productos", e);
        }
    }
}