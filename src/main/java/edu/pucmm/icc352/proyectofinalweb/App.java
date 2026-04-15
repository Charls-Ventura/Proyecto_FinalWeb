package edu.pucmm.icc352.proyectofinalweb;

import edu.pucmm.icc352.proyectofinalweb.config.AppConfig;
import edu.pucmm.icc352.proyectofinalweb.controller.*;
import edu.pucmm.icc352.proyectofinalweb.db.MongoUtil;
import edu.pucmm.icc352.proyectofinalweb.grpc.GrpcServerManager;
import edu.pucmm.icc352.proyectofinalweb.repository.FormularioRepository;
import edu.pucmm.icc352.proyectofinalweb.repository.UsuarioRepository;
import edu.pucmm.icc352.proyectofinalweb.service.AuthService;
import edu.pucmm.icc352.proyectofinalweb.service.FormularioService;
import edu.pucmm.icc352.proyectofinalweb.service.JwtService;
import edu.pucmm.icc352.proyectofinalweb.service.PasswordService;
import edu.pucmm.icc352.proyectofinalweb.service.SeedService;
import edu.pucmm.icc352.proyectofinalweb.ws.SyncWebSocket;
import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        MongoUtil.iniciar();

        UsuarioRepository usuarioRepository = new UsuarioRepository();
        FormularioRepository formularioRepository = new FormularioRepository();

        PasswordService passwordService = new PasswordService();
        JwtService jwtService = new JwtService();
        AuthService authService = new AuthService(usuarioRepository, passwordService, jwtService);
        FormularioService formularioService = new FormularioService(formularioRepository);
        SeedService seedService = new SeedService(authService, usuarioRepository);

        seedService.crearAdminSiNoExiste();

        GrpcServerManager grpcServerManager = new GrpcServerManager(authService, formularioService);
        grpcServerManager.iniciar(AppConfig.grpcPort());

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.showJavalinBanner = false;

            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setMaxTextMessageSize(1024 * 1024);
            });
        });

        new PaginaController(usuarioRepository, formularioRepository).registrar(app);
        new AuthController(authService).registrar(app);
        new FormularioController(authService, formularioService).registrar(app);
        new AdminController(authService, usuarioRepository, formularioRepository).registrar(app);
        new DashboardController(formularioRepository).registrar(app);
        new GrpcClientController(formularioService).registrar(app);
        new SyncWebSocket(authService, formularioService).registrar(app);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            grpcServerManager.detener();
            MongoUtil.cerrar();
        }));

        app.start(AppConfig.httpPort());

        System.out.println("HTTP iniciado en http://localhost:" + AppConfig.httpPort());
        System.out.println("gRPC iniciado en puerto " + AppConfig.grpcPort());
        System.out.println("Usuario admin por defecto: admin / admin123");
    }
}