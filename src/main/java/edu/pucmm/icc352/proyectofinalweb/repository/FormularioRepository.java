package edu.pucmm.icc352.proyectofinalweb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import edu.pucmm.icc352.proyectofinalweb.db.MongoUtil;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class FormularioRepository {
    private final MongoCollection<Document> collection =
            MongoUtil.getBaseDatos().getCollection("formularios");

    public Formulario guardar(Formulario formulario) {
        Document doc = new Document()
                .append("nombre", formulario.getNombre())
                .append("sector", formulario.getSector())
                .append("nivelEscolar", formulario.getNivelEscolar())
                .append("usuario", formulario.getUsuario())
                .append("latitud", formulario.getLatitud())
                .append("longitud", formulario.getLongitud())
                .append("fotoBase64", formulario.getFotoBase64())
                .append("fechaRegistro", formulario.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            formulario.setId(id.toHexString());
        }
        return formulario;
    }

    public List<Formulario> listarTodos() {
        List<Formulario> formularios = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.descending("fechaRegistro"))) {
            formularios.add(mapear(doc));
        }
        return formularios;
    }

    public List<Formulario> listarPorUsuario(String usuario) {
        List<Formulario> formularios = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("usuario", usuario))
                .sort(Sorts.descending("fechaRegistro"))) {
            formularios.add(mapear(doc));
        }
        return formularios;
    }

    private Formulario mapear(Document doc) {
        Long fecha = doc.getLong("fechaRegistro");
        LocalDateTime fechaRegistro = fecha == null
                ? LocalDateTime.now()
                : LocalDateTime.ofInstant(Instant.ofEpochMilli(fecha), ZoneId.systemDefault());

        Formulario formulario = new Formulario();
        formulario.setId(doc.getObjectId("_id").toHexString());
        formulario.setNombre(doc.getString("nombre"));
        formulario.setSector(doc.getString("sector"));
        formulario.setNivelEscolar(doc.getString("nivelEscolar"));
        formulario.setUsuario(doc.getString("usuario"));
        formulario.setLatitud(doc.getDouble("latitud"));
        formulario.setLongitud(doc.getDouble("longitud"));
        formulario.setFotoBase64(doc.getString("fotoBase64"));
        formulario.setFechaRegistro(fechaRegistro);
        return formulario;
    }
}