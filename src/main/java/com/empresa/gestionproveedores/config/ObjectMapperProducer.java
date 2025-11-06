package com.empresa.gestionproveedores.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Producer CDI para ObjectMapper de Jackson
 */
@ApplicationScoped
public class ObjectMapperProducer {

    @Produces
    @ApplicationScoped
    public ObjectMapper produceObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registrar módulo para Java 8 Date/Time API
        mapper.registerModule(new JavaTimeModule());

        // Configuraciones
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        return mapper;
    }
}