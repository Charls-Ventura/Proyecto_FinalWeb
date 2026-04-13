package edu.pucmm.icc352.proyectofinalweb.repository;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;
import edu.pucmm.icc352.proyectofinalweb.db.MongoUtil;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;

import java.time.LocalDateTime;
import java.util.List;

public class FormularioRepository {

    private final Datastore ds = MongoUtil.getDatastore();

    public Formulario guardar(Formulario formulario) {
        if (formulario.getFechaRegistro() == null) {
            formulario.setFechaRegistro(LocalDateTime.now());
        }
        ds.save(formulario);
        return formulario;
    }

    public List<Formulario> listarTodos() {
        return ds.find(Formulario.class)
                .iterator(new FindOptions().sort(Sort.descending("fechaRegistro")))
                .toList();
    }

    public List<Formulario> listarPorUsuario(String usuario) {
        return ds.find(Formulario.class)
                .filter(Filters.eq("usuario", usuario))
                .iterator(new FindOptions().sort(Sort.descending("fechaRegistro")))
                .toList();
    }
}