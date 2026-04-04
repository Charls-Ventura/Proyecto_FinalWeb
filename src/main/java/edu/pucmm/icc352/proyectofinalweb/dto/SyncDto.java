package edu.pucmm.icc352.proyectofinalweb.dto;

import java.util.ArrayList;
import java.util.List;

public class SyncDto {
    private String token;
    private List<FormularioDto> formularios = new ArrayList<>();

    public SyncDto() {
    }

    public String getToken() {
        return token;
    }

    public List<FormularioDto> getFormularios() {
        return formularios;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFormularios(List<FormularioDto> formularios) {
        this.formularios = formularios;
    }
}
