package edu.pucmm.icc352.proyectofinalweb.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Entity("formularios")
public class Formulario {

    @Id
    private ObjectId id;
    private String nombre;
    private String sector;
    private String nivelEscolar;
    private String usuario;
    private double latitud;
    private double longitud;
    private String fotoBase64;
    private LocalDateTime fechaRegistro;

    public Formulario() {
    }

    public Formulario(String id, String nombre, String sector, String nivelEscolar, String usuario,
                      double latitud, double longitud, String fotoBase64, LocalDateTime fechaRegistro) {
        this.id = id != null ? new ObjectId(id) : null;
        this.nombre = nombre;
        this.sector = sector;
        this.nivelEscolar = nivelEscolar;
        this.usuario = usuario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fotoBase64 = fotoBase64;
        this.fechaRegistro = fechaRegistro;
    }

    public String getId() {
        return id != null ? id.toHexString() : null;
    }

    public void setId(String id) {
        this.id = id != null ? new ObjectId(id) : null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getNivelEscolar() {
        return nivelEscolar;
    }

    public void setNivelEscolar(String nivelEscolar) {
        this.nivelEscolar = nivelEscolar;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}