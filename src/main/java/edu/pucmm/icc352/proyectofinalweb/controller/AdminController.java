package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import edu.pucmm.icc352.proyectofinalweb.repository.FormularioRepository;
import edu.pucmm.icc352.proyectofinalweb.repository.UsuarioRepository;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.util.SesionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final FormularioRepository formularioRepository;

    public AdminController(AuthService authService, UsuarioRepository usuarioRepository,
                           FormularioRepository formularioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.formularioRepository = formularioRepository;
    }

    public void registrar(Javalin app) {
        app.post("/admin/usuarios", this::crearUsuario);
    }

    private void crearUsuario(Context ctx) {
        if (!SesionUtil.esAdmin(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }

        String username = ctx.formParam("username") != null ? ctx.formParam("username").trim() : "";
        String password = ctx.formParam("password") != null ? ctx.formParam("password").trim() : "";
        String rolTexto = ctx.formParam("rol") != null ? ctx.formParam("rol").trim() : "ENCUESTADOR";

        if (username.isBlank() || password.isBlank() || authService.existeUsuario(username)) {
            ctx.redirect("/admin");
            return;
        }

        Rol rol = Rol.valueOf(rolTexto);
        authService.crearUsuario(username, password, rol);
        ctx.redirect("/admin");
    }
}
