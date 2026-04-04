package edu.pucmm.icc352.proyectofinalweb.util;

import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import io.javalin.http.Context;

public final class SesionUtil {
    private SesionUtil() {
    }

    public static boolean estaLogueado(Context ctx) {
        return ctx.sessionAttribute("username") != null;
    }

    public static String username(Context ctx) {
        String username = ctx.sessionAttribute("username");
        return username == null ? "" : username;
    }

    public static Rol rol(Context ctx) {
        String rol = ctx.sessionAttribute("rol");
        return rol == null ? null : Rol.valueOf(rol);
    }

    public static boolean esAdmin(Context ctx) {
        return rol(ctx) == Rol.ADMIN;
    }

    public static void iniciarSesion(Context ctx, String username, Rol rol) {
        ctx.sessionAttribute("username", username);
        ctx.sessionAttribute("rol", rol.name());
    }

    public static void cerrarSesion(Context ctx) {
        var session = ctx.req().getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
