package edu.pucmm.icc352.proyectofinalweb.repository;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import dev.morphia.query.filters.Filters;
import edu.pucmm.icc352.proyectofinalweb.db.MongoUtil;
import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;

import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private final Datastore ds = MongoUtil.getDatastore();

    public Optional<Usuario> buscarPorUsername(String username) {
        return Optional.ofNullable(
                ds.find(Usuario.class)
                        .filter(Filters.eq("username", username))
                        .first()
        );
    }

    public Usuario guardar(Usuario usuario) {
        ds.save(usuario);
        return usuario;
    }

    public List<Usuario> listarTodos() {
        return ds.find(Usuario.class)
                .iterator(new FindOptions().sort(Sort.ascending("username")))
                .toList();
    }

    public long contarAdmins() {
        return ds.find(Usuario.class)
                .filter(Filters.eq("rol", Rol.ADMIN))
                .count();
    }
}