package edu.pucmm.icc352.proyectofinalweb.service;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import edu.pucmm.icc352.proyectofinalweb.repository.FormularioRepository;

import java.time.LocalDateTime;
import java.util.List;

public class FormularioService {
    private final FormularioRepository formularioRepository;

    public FormularioService(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    public Formulario crear(String usuario, FormularioDto dto) {
        Formulario formulario = new Formulario();
        formulario.setNombre(dto.getNombre());
        formulario.setSector(dto.getSector());
        formulario.setNivelEscolar(dto.getNivelEscolar());
        formulario.setUsuario(usuario);
        formulario.setLatitud(dto.getLatitud());
        formulario.setLongitud(dto.getLongitud());
        formulario.setFotoBase64(dto.getFotoBase64());
        formulario.setFechaRegistro(LocalDateTime.now());
        return formularioRepository.guardar(formulario);
    }

    public List<Formulario> listarTodos() {
        return formularioRepository.listarTodos();
    }

    public List<Formulario> listarPorUsuario(String usuario) {
        return formularioRepository.listarPorUsuario(usuario);
    }
}
