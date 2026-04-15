package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import edu.pucmm.icc352.proyectofinalweb.util.JsonUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GrpcClientController {

    private final FormularioService formularioService;

    public GrpcClientController(FormularioService formularioService) {
        this.formularioService = formularioService;
    }

    public void registrar(Javalin app) {
        app.get("/api/grpc/formularios/listar", this::listar);
        app.post("/api/grpc/formularios/crear", this::crear);
    }

    private void listar(Context ctx) {
        try {
            List<Formulario> formularios = formularioService.listarTodos();
            responder(ctx, 200, true, "Listado gRPC correcto", formularios);
        } catch (Exception e) {
            responder(ctx, 500, false, "No se pudo listar por gRPC", null);
        }
    }

    private void crear(Context ctx) {
        try {
            FormularioDto dto = JsonUtil.fromJson(ctx.body(), FormularioDto.class);

            String username = dto.getUsername() != null ? dto.getUsername().trim() : "";
            if (username.isBlank()) {
                responder(ctx, 400, false, "Debe indicar el usuario encuestador", null);
                return;
            }

            Formulario formulario = formularioService.crear(username, dto);
            responder(ctx, 200, true, "Formulario creado vía gRPC", formulario);
        } catch (Exception e) {
            responder(ctx, 400, false, "No se pudo crear el formulario vía gRPC", null);
        }
    }

    private void responder(Context ctx, int status, boolean ok, String mensaje, Object datos) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("ok", ok);
        respuesta.put("mensaje", mensaje);
        respuesta.put("datos", datos);
        ctx.status(status).contentType("application/json").result(JsonUtil.toJson(respuesta));
    }
}