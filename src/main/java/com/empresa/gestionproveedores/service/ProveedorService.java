package com.empresa.gestionproveedores.service;

import com.empresa.gestionproveedores.config.ApiConfig;
import com.empresa.gestionproveedores.dto.ApiResponseDTO;
import com.empresa.gestionproveedores.dto.ProveedorDTO;
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
 * Servicio para consumir API REST de Proveedores
 */
@ApplicationScoped
@Slf4j
public class ProveedorService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ApiConfig apiConfig;

    @Inject
    private CloseableHttpClient httpClient;

    @Inject
    private ObjectMapper objectMapper;

    /**
     * Lista todos los proveedores
     */
    public List<ProveedorDTO> listarTodos() {
        try {
            String url = apiConfig.getProveedoresUrl();
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProveedorDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProveedorDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar proveedores", e);
            throw new ServiceException("Error al obtener la lista de proveedores", e);
        }
    }

    /**
     * Lista proveedores activos
     */
    public List<ProveedorDTO> listarActivos() {
        try {
            String url = apiConfig.getProveedoresUrl() + "/activos";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProveedorDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProveedorDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al listar proveedores activos", e);
            throw new ServiceException("Error al obtener proveedores activos", e);
        }
    }

    /**
     * Busca un proveedor por ID
     */
    public ProveedorDTO buscarPorId(Long id) {
        try {
            String url = apiConfig.getProveedoresUrl() + "/" + id;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProveedorDTO> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<ProveedorDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar proveedor por ID: {}", id, e);
            throw new ServiceException("Error al buscar el proveedor", e);
        }
    }

    /**
     * Busca proveedores por razón social
     */
    public List<ProveedorDTO> buscarPorRazonSocial(String razonSocial) {
        try {
            String url = apiConfig.getProveedoresUrl() + "/buscar?razonSocial=" + razonSocial;
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<List<ProveedorDTO>> apiResponse = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponseDTO<List<ProveedorDTO>>>() {}
                );

                return apiResponse.getData() != null ? apiResponse.getData() : new ArrayList<>();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al buscar proveedores por razón social", e);
            throw new ServiceException("Error al buscar proveedores", e);
        }
    }

    /**
     * Crea un nuevo proveedor
     */
    public ProveedorDTO crear(ProveedorDTO proveedorDTO) {
        try {
            String url = apiConfig.getProveedoresUrl();
            HttpPost request = new HttpPost(url);

            String json = objectMapper.writeValueAsString(proveedorDTO);
            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseJson = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProveedorDTO> apiResponse = objectMapper.readValue(
                        responseJson,
                        new TypeReference<ApiResponseDTO<ProveedorDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al crear proveedor", e);
            throw new ServiceException("Error al crear el proveedor", e);
        }
    }

    /**
     * Actualiza un proveedor existente
     */
    public ProveedorDTO actualizar(Long id, ProveedorDTO proveedorDTO) {
        try {
            String url = apiConfig.getProveedoresUrl() + "/" + id;
            HttpPut request = new HttpPut(url);

            String json = objectMapper.writeValueAsString(proveedorDTO);
            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseJson = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProveedorDTO> apiResponse = objectMapper.readValue(
                        responseJson,
                        new TypeReference<ApiResponseDTO<ProveedorDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al actualizar proveedor ID: {}", id, e);
            throw new ServiceException("Error al actualizar el proveedor", e);
        }
    }

    /**
     * Elimina un proveedor
     */
    public void eliminar(Long id) {
        try {
            String url = apiConfig.getProveedoresUrl() + "/" + id;
            HttpDelete request = new HttpDelete(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode != 200 && statusCode != 204) {
                    throw new ServiceException("Error al eliminar proveedor. Código: " + statusCode);
                }
            }
        } catch (IOException e) {
            log.error("Error al eliminar proveedor ID: {}", id, e);
            throw new ServiceException("Error al eliminar el proveedor", e);
        }
    }

    /**
     * Cambia el estado de un proveedor
     */
    public ProveedorDTO cambiarEstado(Long id, Boolean activo) {
        try {
            String url = apiConfig.getProveedoresUrl() + "/" + id + "/estado?activo=" + activo;
            HttpPatch request = new HttpPatch(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseJson = EntityUtils.toString(response.getEntity());

                ApiResponseDTO<ProveedorDTO> apiResponse = objectMapper.readValue(
                        responseJson,
                        new TypeReference<ApiResponseDTO<ProveedorDTO>>() {}
                );

                return apiResponse.getData();
            }
        } catch (IOException | ParseException e) {
            log.error("Error al cambiar estado del proveedor ID: {}", id, e);
            throw new ServiceException("Error al cambiar el estado del proveedor", e);
        }
    }
}