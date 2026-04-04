package edu.pucmm.icc352.proyectofinalweb.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import edu.pucmm.icc352.proyectofinalweb.config.AppConfig;

public final class MongoUtil {
    private static MongoClient cliente;
    private static MongoDatabase baseDatos;

    private MongoUtil() {
    }

    public static void iniciar() {
        if (cliente == null) {
            cliente = MongoClients.create(AppConfig.mongoUrl());
            baseDatos = cliente.getDatabase(AppConfig.dbName());
        }
    }

    public static MongoDatabase getBaseDatos() {
        if (baseDatos == null) {
            iniciar();
        }
        return baseDatos;
    }

    public static void cerrar() {
        if (cliente != null) {
            cliente.close();
        }
    }
}
