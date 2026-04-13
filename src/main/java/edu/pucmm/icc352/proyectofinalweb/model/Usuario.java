package edu.pucmm.icc352.proyectofinalweb.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

@Entity("usuarios")
public class Usuario {

    @Id
    private ObjectId id;

    @Indexed(options = @IndexOptions(unique = true))
    private String username;

    private String passwordHash;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String username, String passwordHash, Rol rol) {
        this.id = id != null ? new ObjectId(id) : null;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getId() {
        return id != null ? id.toHexString() : null;
    }

    public void setId(String id) {
        this.id = id != null ? new ObjectId(id) : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}