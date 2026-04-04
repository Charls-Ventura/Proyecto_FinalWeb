package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.repository.FormularioRepository;
import edu.pucmm.icc352.proyectofinalweb.repository.UsuarioRepository;
import edu.pucmm.icc352.proyectofinalweb.util.SesionUtil;
import edu.pucmm.icc352.proyectofinalweb.util.VistaUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class PaginaController {
    private final UsuarioRepository usuarioRepository;
    private final FormularioRepository formularioRepository;

    public PaginaController(UsuarioRepository usuarioRepository, FormularioRepository formularioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.formularioRepository = formularioRepository;
    }

    public void registrar(Javalin app) {
        app.get("/", this::inicio);
        app.get("/login", this::login);
        app.get("/dashboard", this::dashboard);
        app.get("/admin", this::admin);
        app.get("/mapa", this::mapa);
    }

    private void inicio(Context ctx) {
        if (SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/dashboard");
        } else {
            ctx.redirect("/login");
        }
    }

    private void login(Context ctx) {
        if (SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", "Proyecto Final Web");
        ctx.html(VistaUtil.render("login.html", datos));
    }

    private void dashboard(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/login");
            return;
        }
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", "Panel del encuestador");
        datos.put("username", SesionUtil.username(ctx));
        datos.put("rol", SesionUtil.rol(ctx).name());
        ctx.html(VistaUtil.render("dashboard.html", datos));
    }

    private void admin(Context ctx) {
        if (!SesionUtil.esAdmin(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", "Administración");
        datos.put("username", SesionUtil.username(ctx));
        datos.put("usuarios", usuarioRepository.listarTodos());
        datos.put("formularios", formularioRepository.listarTodos());
        ctx.html(VistaUtil.render("admin.html", datos));
    }

    private void mapa(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/login");
            return;
        }
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", "Mapa de registros");
        datos.put("username", SesionUtil.username(ctx));
        ctx.html(VistaUtil.render("mapa.html", datos));
    }
}
