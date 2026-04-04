package edu.pucmm.icc352.proyectofinalweb.grpc;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.generated.CrearFormularioRequest;
import edu.pucmm.icc352.proyectofinalweb.generated.FormularioGrpcServiceGrpc;
import edu.pucmm.icc352.proyectofinalweb.generated.FormularioItem;
import edu.pucmm.icc352.proyectofinalweb.generated.FormularioResponse;
import edu.pucmm.icc352.proyectofinalweb.generated.ListaFormulariosResponse;
import edu.pucmm.icc352.proyectofinalweb.generated.ListarPorUsuarioRequest;
import edu.pucmm.icc352.proyectofinalweb.model.Formulario;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;

public class FormularioGrpcService extends FormularioGrpcServiceGrpc.FormularioGrpcServiceImplBase {
    private final AuthService authService;
    private final FormularioService formularioService;

    public FormularioGrpcService(AuthService authService, FormularioService formularioService) {
        this.authService = authService;
        this.formularioService = formularioService;
    }

    @Override
    public void listarPorUsuario(ListarPorUsuarioRequest request,
                                 StreamObserver<ListaFormulariosResponse> responseObserver) {
        Optional<Usuario> usuario = authService.validarTokenYBuscarUsuario(request.getToken());

        if (usuario.isEmpty()) {
            responseObserver.onNext(ListaFormulariosResponse.newBuilder()
                    .setOk(false)
                    .setMensaje("Token inválido")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        List<Formulario> formularios = formularioService.listarPorUsuario(usuario.get().getUsername());
        ListaFormulariosResponse.Builder builder = ListaFormulariosResponse.newBuilder()
                .setOk(true)
                .setMensaje("Listado correcto");

        for (Formulario formulario : formularios) {
            builder.addFormularios(FormularioItem.newBuilder()
                    .setId(formulario.getId())
                    .setNombre(formulario.getNombre())
                    .setSector(formulario.getSector())
                    .setNivelEscolar(formulario.getNivelEscolar())
                    .setUsuario(formulario.getUsuario())
                    .setLatitud(formulario.getLatitud())
                    .setLongitud(formulario.getLongitud())
                    .setFechaRegistro(formulario.getFechaRegistro().toString())
                    .build());
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void crearFormulario(CrearFormularioRequest request,
                                StreamObserver<FormularioResponse> responseObserver) {
        Optional<Usuario> usuario = authService.validarTokenYBuscarUsuario(request.getToken());

        if (usuario.isEmpty()) {
            responseObserver.onNext(FormularioResponse.newBuilder()
                    .setOk(false)
                    .setMensaje("Token inválido")
                    .build());
            responseObserver.onCompleted();
            return;
        }

        FormularioDto dto = new FormularioDto();
        dto.setNombre(request.getNombre());
        dto.setSector(request.getSector());
        dto.setNivelEscolar(request.getNivelEscolar());
        dto.setLatitud(request.getLatitud());
        dto.setLongitud(request.getLongitud());
        dto.setFotoBase64(request.getFotoBase64());

        Formulario formulario = formularioService.crear(usuario.get().getUsername(), dto);

        responseObserver.onNext(FormularioResponse.newBuilder()
                .setOk(true)
                .setMensaje("Formulario creado")
                .setId(formulario.getId())
                .build());
        responseObserver.onCompleted();
    }
}
