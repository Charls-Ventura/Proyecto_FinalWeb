package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import edu.pucmm.icc352.proyectofinalweb.util.JsonUtil;
import edu.pucmm.icc352.proyectofinalweb.util.SesionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FormularioController {
    private final AuthService authService;
    private final FormularioService formularioService;

    public FormularioController(AuthService authService, FormularioService formularioService) {
        this.authService = authService;
        this.formularioService = formularioService;
    }

    public void registrar(Javalin app) {
        app.post("/api/formularios", this::crearFormularioApi);
        app.get("/api/formularios/mios", this::listarMiosApi);
        app.get("/api/formularios/mapa", this::listarParaMapa);
    }

    private void crearFormularioApi(Context ctx) {
        String token = extraerToken(ctx);
        Optional<Usuario> usuario = authService.validarTokenYBuscarUsuario(token);
        if (usuario.isEmpty()) {
            responder(ctx, 401, false, "Token inválido", null);
            return;
        }

        try {
            FormularioDto dto = JsonUtil.fromJson(ctx.body(), FormularioDto.class);
            Formulario formulario = formularioService.crear(usuario.get().getUsername(), dto);
            responder(ctx, 200, true, "Formulario guardado", formulario);
        } catch (Exception e) {
            responder(ctx, 400, false, "No se pudo guardar el formulario", null);
        }
    }

    private void listarMiosApi(Context ctx) {
        String token = extraerToken(ctx);
        Optional<Usuario> usuario = authService.validarTokenYBuscarUsuario(token);
        if (usuario.isEmpty()) {
            responder(ctx, 401, false, "Token inválido", null);
            return;
        }

        List<Formulario> formularios = formularioService.listarPorUsuario(usuario.get().getUsername());
        responder(ctx, 200, true, "Listado correcto", formularios);
    }

    private void listarParaMapa(Context ctx) {
        if (!SesionUtil.estaLogueado(ctx)) {
            responder(ctx, 401, false, "Debe iniciar sesión", null);
            return;
        }
        responder(ctx, 200, true, "Listado correcto", formularioService.listarTodos());
    }

    private String extraerToken(Context ctx) {
        String auth = ctx.header("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring("Bearer ".length()).trim();
        }
        return "";
    }

    private void responder(Context ctx, int status, boolean ok, String mensaje, Object datos) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("ok", ok);
        respuesta.put("mensaje", mensaje);
        respuesta.put("datos", datos);
        ctx.status(status).contentType("application/json").result(JsonUtil.toJson(respuesta));
    }
}
