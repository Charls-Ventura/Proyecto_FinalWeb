package edu.pucmm.icc352.proyectofinalweb.model;

public class Usuario {
    private String id;
    private String username;
    private String passwordHash;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String username, String passwordHash, Rol rol) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}