package edu.pucmm.icc352.proyectofinalweb.client.grpc;

import edu.pucmm.icc352.proyectofinalweb.generated.CrearFormularioRequest;
import edu.pucmm.icc352.proyectofinalweb.generated.ListarPorUsuarioRequest;
import edu.pucmm.icc352.proyectofinalweb.generated.FormularioGrpcServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClientService {
    public String listar(String host, int puerto, String token) {
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forAddress(host, puerto)
                    .usePlaintext()
                    .build();

            FormularioGrpcServiceGrpc.FormularioGrpcServiceBlockingStub stub =
                    FormularioGrpcServiceGrpc.newBlockingStub(channel);

            return stub.listarPorUsuario(ListarPorUsuarioRequest.newBuilder()
                    .setToken(token)
                    .build()).toString();
        } finally {
            if (channel != null) {
                channel.shutdownNow();
            }
        }
    }

    public String crear(String host, int puerto, String token, String nombre, String sector,
                        String nivelEscolar, double latitud, double longitud, String fotoBase64) {
        ManagedChannel channel = null;
        try {
            channel = ManagedChannelBuilder.forAddress(host, puerto)
                    .usePlaintext()
                    .build();

            FormularioGrpcServiceGrpc.FormularioGrpcServiceBlockingStub stub =
                    FormularioGrpcServiceGrpc.newBlockingStub(channel);

            return stub.crearFormulario(CrearFormularioRequest.newBuilder()
                    .setToken(token)
                    .setNombre(nombre)
                    .setSector(sector)
                    .setNivelEscolar(nivelEscolar)
                    .setLatitud(latitud)
                    .setLongitud(longitud)
                    .setFotoBase64(fotoBase64 == null ? "" : fotoBase64)
                    .build()).toString();
        } finally {
            if (channel != null) {
                channel.shutdownNow();
            }
        }
    }
}
