package edu.pucmm.icc352.proyectofinalweb.controller;

import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import edu.pucmm.icc352.proyectofinalweb.repository.FormularioRepository;
import io.javalin.Javalin;

import java.util.*;

public class DashboardController {

    private final FormularioRepository formularioRepository;

    public DashboardController(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    public void registrar(Javalin app) {
        app.get("/api/dashboard/resumen", ctx -> {

            List<Formulario> lista = formularioRepository.listarTodos();

            Map<String, Long> niveles = new HashMap<>();
            Map<String, Long> sectores = new HashMap<>();
            Set<String> usuarios = new HashSet<>();

            for (Formulario f : lista) {

                // niveles
                String nivel = f.getNivelEscolar();
                if (nivel != null) {
                    niveles.put(nivel, niveles.getOrDefault(nivel, 0L) + 1);
                }

                // sectores
                String sector = f.getSector();
                if (sector != null) {
                    sectores.put(sector, sectores.getOrDefault(sector, 0L) + 1);
                }

                // usuarios
                if (f.getUsuario() != null) {
                    usuarios.add(f.getUsuario());
                }
            }

            List<Map<String, Object>> topSectores = sectores.entrySet().stream()
                    .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                    .limit(5)
                    .map(e -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("nombre", e.getKey());
                        m.put("cantidad", e.getValue());
                        return m;
                    })
                    .toList();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("totalFormularios", lista.size());
            respuesta.put("totalSectores", sectores.size());
            respuesta.put("totalUsuarios", usuarios.size());
            respuesta.put("niveles", niveles);
            respuesta.put("topSectores", topSectores);

            ctx.json(respuesta);
        });
    }
}