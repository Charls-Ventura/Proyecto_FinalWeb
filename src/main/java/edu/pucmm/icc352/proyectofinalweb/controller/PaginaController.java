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
        app.get("/encuesta", this::encuesta);
        app.get("/admin", this::admin);
        app.get("/mapa", this::mapa);
        app.get("/rest-client", this::restClient);
        app.get("/grpc-client", this::grpcClient);
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
        ctx.html(VistaUtil.render("login", datos));
    }

    private void dashboard(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/login");
            return;
        }

        String rol = SesionUtil.rol(ctx).name();

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", rol);

        if ("ADMIN".equals(rol)) {
            modelo.put("titulo", "Dashboard");
            ctx.html(VistaUtil.render("dashboard.html", modelo));
        } else {
            modelo.put("titulo", "Mi panel");
            ctx.html(VistaUtil.render("dashboard-encuestador.html", modelo));
        }
    }

    private void encuesta(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Encuesta");
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", SesionUtil.rol(ctx).name());

        ctx.html(VistaUtil.render("encuesta.html", modelo));
    }

    private void admin(Context ctx) {
        if (!SesionUtil.esAdmin(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Administración");
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", SesionUtil.rol(ctx).name());
        modelo.put("usuarios", usuarioRepository.listarTodos());
        modelo.put("formularios", formularioRepository.listarTodos());

        ctx.html(VistaUtil.render("admin.html", modelo));
    }

    private void mapa(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Mapa de registros");
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", SesionUtil.rol(ctx).name());

        ctx.html(VistaUtil.render("mapa.html", modelo));
    }

    private void restClient(Context ctx) {
        if (!SesionUtil.esAdmin(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Cliente REST");
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", SesionUtil.rol(ctx).name());

        ctx.html(VistaUtil.render("rest-client.html", modelo));
    }

    private void grpcClient(Context ctx) {
        if (!SesionUtil.esAdmin(ctx)) {
            ctx.redirect("/dashboard");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Cliente gRPC");
        modelo.put("username", SesionUtil.username(ctx));
        modelo.put("rol", SesionUtil.rol(ctx).name());

        ctx.html(VistaUtil.render("grpc-client.html", modelo));
    }
}

