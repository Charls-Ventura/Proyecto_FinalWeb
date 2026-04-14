package edu.pucmm.icc352.proyectofinalweb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import edu.pucmm.icc352.proyectofinalweb.db.MongoUtil;
import edu.pucmm.icc352.proyectofinalweb.model.Rol;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    private final MongoCollection<Document> collection =
            MongoUtil.getBaseDatos().getCollection("usuarios");

    public Optional<Usuario> buscarPorUsername(String username) {
        Document doc = collection.find(Filters.eq("username", username)).first();
        return Optional.ofNullable(doc).map(this::mapear);
    }

    public Usuario guardar(Usuario usuario) {
        Document doc = new Document()
                .append("username", usuario.getUsername())
                .append("passwordHash", usuario.getPasswordHash())
                .append("rol", usuario.getRol().name());

        if (usuario.getId() == null || usuario.getId().isBlank()) {
            collection.insertOne(doc);
            ObjectId id = doc.getObjectId("_id");
            if (id != null) {
                usuario.setId(id.toHexString());
            }
        } else {
            doc.put("_id", new ObjectId(usuario.getId()));
            collection.replaceOne(Filters.eq("_id", new ObjectId(usuario.getId())), doc);
        }
        return usuario;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.ascending("username"))) {
            usuarios.add(mapear(doc));
        }
        return usuarios;
    }

    public long contarAdmins() {
        return collection.countDocuments(Filters.eq("rol", Rol.ADMIN.name()));
    }

    private Usuario mapear(Document doc) {
        return new Usuario(
                doc.getObjectId("_id").toHexString(),
                doc.getString("username"),
                doc.getString("passwordHash"),
                Rol.valueOf(doc.getString("rol"))
        );
    }
}