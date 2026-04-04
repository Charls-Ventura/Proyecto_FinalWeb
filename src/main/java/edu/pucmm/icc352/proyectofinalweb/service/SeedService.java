package edu.pucmm.icc352.proyectofinalweb.service;

import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import edu.pucmm.icc352.proyectofinalweb.repository.UsuarioRepository;

public class SeedService {
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public SeedService(AuthService authService, UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    public void crearAdminSiNoExiste() {
        if (usuarioRepository.contarAdmins() == 0) {
            authService.crearUsuario("admin", "admin123", Rol.ADMIN);
        }
    }
}
