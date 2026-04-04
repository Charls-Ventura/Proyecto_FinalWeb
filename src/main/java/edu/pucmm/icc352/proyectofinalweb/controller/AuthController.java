package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.dto.LoginDto;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.util.JsonUtil;
import edu.pucmm.icc352.proyectofinalweb.util.SesionUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void registrar(Javalin app) {
        app.post("/api/auth/login", this::loginApi);
        app.get("/logout", this::logout);
    }

    private void loginApi(Context ctx) {
        try {
            LoginDto dto = JsonUtil.fromJson(ctx.body(), LoginDto.class);
            Optional<Usuario> usuario = authService.autenticar(dto.getUsername(), dto.getPassword());

            if (usuario.isEmpty()) {
                responder(ctx, 401, false, "Usuario o clave incorrectos", null, null, null);
                return;
            }

            Usuario user = usuario.get();
            String token = authService.generarToken(user);
            SesionUtil.iniciarSesion(ctx, user.getUsername(), user.getRol());

            responder(ctx, 200, true, "Login correcto", token, user.getUsername(), user.getRol().name());
        } catch (Exception e) {
            responder(ctx, 400, false, "No se pudo iniciar sesión", null, null, null);
        }
    }

    private void logout(Context ctx) {
        SesionUtil.cerrarSesion(ctx);
        ctx.redirect("/login");
    }

    private void responder(Context ctx, int status, boolean ok, String mensaje, String token, String username, String rol) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ok", ok);
        data.put("mensaje", mensaje);
        data.put("token", token);
        data.put("username", username);
        data.put("rol", rol);
        ctx.status(status).contentType("application/json").result(JsonUtil.toJson(data));
    }
}
