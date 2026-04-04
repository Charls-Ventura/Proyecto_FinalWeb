package edu.pucmm.icc352.proyectofinalweb.dto;

public class FormularioDto {
    private String nombre;
    private String sector;
    private String nivelEscolar;
    private double latitud;
    private double longitud;
    private String fotoBase64;

    public FormularioDto() {
    }

    public String getNombre() {
        return nombre;
    }

    public String getSector() {
        return sector;
    }

    public String getNivelEscolar() {
        return nivelEscolar;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setNivelEscolar(String nivelEscolar) {
        this.nivelEscolar = nivelEscolar;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
    }
}
