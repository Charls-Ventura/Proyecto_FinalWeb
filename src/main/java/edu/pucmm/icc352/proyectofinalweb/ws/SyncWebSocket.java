package edu.pucmm.icc352.proyectofinalweb.ws;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.dto.SyncDto;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import edu.pucmm.icc352.proyectofinalweb.util.JsonUtil;
import io.javalin.Javalin;
import io.javalin.websocket.WsMessageContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SyncWebSocket {
    private final AuthService authService;
    private final FormularioService formularioService;

    public SyncWebSocket(AuthService authService, FormularioService formularioService) {
        this.authService = authService;
        this.formularioService = formularioService;
    }

    public void registrar(Javalin app) {
        app.ws("/ws/sync", ws -> ws.onMessage(this::procesarMensaje));
    }

    private void procesarMensaje(WsMessageContext  ctx) {
        try {
            SyncDto syncDto = JsonUtil.fromJson(ctx.message(), SyncDto.class);
            Optional<Usuario> usuario = authService.validarTokenYBuscarUsuario(syncDto.getToken());
            if (usuario.isEmpty()) {
                enviar(ctx, false, "Token inválido", 0);
                return;
            }

            int guardados = 0;
            for (FormularioDto dto : syncDto.getFormularios()) {
                formularioService.crear(usuario.get().getUsername(), dto);
                guardados++;
            }
            enviar(ctx, true, "Sincronización completada", guardados);
        } catch (Exception e) {
            enviar(ctx, false, "No se pudo sincronizar", 0);
        }
    }

    private void enviar(WsMessageContext ctx, boolean ok, String mensaje, int cantidad) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("ok", ok);
        respuesta.put("mensaje", mensaje);
        respuesta.put("cantidad", cantidad);
        ctx.send(JsonUtil.toJson(respuesta));
    }
}
