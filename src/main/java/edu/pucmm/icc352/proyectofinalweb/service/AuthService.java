package edu.pucmm.icc352.proyectofinalweb.service;

import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import edu.pucmm.icc352.proyectofinalweb.repository.UsuarioRepository;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordService passwordService, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public Optional<Usuario> autenticar(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.buscarPorUsername(username);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        return passwordService.coincide(password, usuario.get().getPasswordHash()) ? usuario : Optional.empty();
    }

    public boolean existeUsuario(String username) {
        return usuarioRepository.buscarPorUsername(username).isPresent();
    }

    public Usuario crearUsuario(String username, String password, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username.trim());
        usuario.setPasswordHash(passwordService.hash(password.trim()));
        usuario.setRol(rol);
        return usuarioRepository.guardar(usuario);
    }

    public String generarToken(Usuario usuario) {
        return jwtService.generarToken(usuario);
    }

    public Optional<Usuario> validarTokenYBuscarUsuario(String token) {
        if (token == null || token.isBlank() || !jwtService.esValido(token)) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorUsername(jwtService.usernameDesdeToken(token));
    }
}
