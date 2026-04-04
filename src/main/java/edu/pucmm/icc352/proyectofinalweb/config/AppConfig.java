package edu.pucmm.icc352.proyectofinalweb.config;

public final class AppConfig {
    private AppConfig() {
    }

    public static int httpPort() {
        return leerEntero("APP_PORT", 7000);
    }

    public static int grpcPort() {
        return leerEntero("GRPC_PORT", 50051);
    }

    public static String mongoUrl() {
        return leerTexto("MONGO_URL", "mongodb://localhost:27017");
    }

    public static String dbName() {
        return leerTexto("MONGO_DB", "proyecto_final_web");
    }

    public static String jwtSecret() {
        return leerTexto("JWT_SECRET", "clave-super-secreta-cambiar-en-produccion-2026");
    }

    private static int leerEntero(String clave, int porDefecto) {
        String valor = System.getenv(clave);
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private static String leerTexto(String clave, String porDefecto) {
        String valor = System.getenv(clave);
        return (valor == null || valor.isBlank()) ? porDefecto : valor.trim();
    }
}
