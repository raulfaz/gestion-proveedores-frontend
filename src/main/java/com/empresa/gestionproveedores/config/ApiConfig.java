package com.empresa.gestionproveedores.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@ApplicationScoped
@Getter
public class ApiConfig {

    private final String baseUrl = "http://localhost:8081/api";

    // Endpoints
    public String getProveedoresUrl() {
        return baseUrl + "/proveedores";
    }

    public String getProductosUrl() {
        return baseUrl + "/productos";
    }

    public String getOrdenesCompraUrl() {
        return baseUrl + "/ordenes-compra";
    }

    // Timeouts
    private final int connectionTimeout = 30000; // 30 segundos
    private final int socketTimeout = 30000;
}