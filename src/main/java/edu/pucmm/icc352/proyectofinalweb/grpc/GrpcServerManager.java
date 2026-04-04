package edu.pucmm.icc352.proyectofinalweb.grpc;

import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServerManager {
    private final AuthService authService;
    private final FormularioService formularioService;
    private Server server;

    public GrpcServerManager(AuthService authService, FormularioService formularioService) {
        this.authService = authService;
        this.formularioService = formularioService;
    }

    public void iniciar(int puerto) {
        try {
            server = ServerBuilder.forPort(puerto)
                    .addService(new FormularioGrpcService(authService, formularioService))
                    .build()
                    .start();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo iniciar gRPC", e);
        }
    }

    public void detener() {
        if (server != null) {
            server.shutdownNow();
        }
    }
}
