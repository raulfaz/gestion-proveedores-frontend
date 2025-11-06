package com.empresa.gestionproveedores.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;

/**
 * Producer CDI para Apache HttpClient
 */
@ApplicationScoped
public class HttpClientProducer {

    @Produces
    @ApplicationScoped
    public CloseableHttpClient produceHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .build();
    }
}