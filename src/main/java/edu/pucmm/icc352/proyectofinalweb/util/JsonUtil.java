package edu.pucmm.icc352.proyectofinalweb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule()); // 🔥 ESTA LINEA ES LA CLAVE
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtil() {
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo convertir a JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clase) {
        try {
            return MAPPER.readValue(json, clase);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer el JSON", e);
        }
    }
}