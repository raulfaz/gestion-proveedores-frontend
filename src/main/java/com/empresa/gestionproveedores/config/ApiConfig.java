package com.empresa.gestionproveedores.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

/**
 * Configuración de la API REST
 */
@ApplicationScoped
@Getter
public class ApiConfig {

    private final String baseUrl = "http://localhost:8081/api";
    private final String proveedoresEndpoint = "/proveedores";
    private final String productosEndpoint = "/productos";
    private final String ordenesEndpoint = "/ordenes-compra";

    public String getProveedoresUrl() {
        return baseUrl + proveedoresEndpoint;
    }

    public String getProductosUrl() {
        return baseUrl + productosEndpoint;
    }

    public String getOrdenesUrl() {
        return baseUrl + ordenesEndpoint;
    }
}