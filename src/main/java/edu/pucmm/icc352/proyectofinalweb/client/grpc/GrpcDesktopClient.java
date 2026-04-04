package edu.pucmm.icc352.proyectofinalweb.client.grpc;

import javax.swing.*;
import java.awt.*;

public class GrpcDesktopClient {
    private final GrpcClientService service = new GrpcClientService();

    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("50051");
    private final JTextField tokenField = new JTextField();
    private final JTextField nombreField = new JTextField("Cliente gRPC");
    private final JTextField sectorField = new JTextField("Santiago");
    private final JTextField nivelField = new JTextField("Grado Universitario");
    private final JTextField latField = new JTextField("19.4517");
    private final JTextField lngField = new JTextField("-70.6970");
    private final JTextArea salida = new JTextArea();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GrpcDesktopClient().mostrar());
    }

    private void mostrar() {
        JFrame frame = new JFrame("Cliente gRPC - Proyecto Final Web");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Host"));
        formulario.add(hostField);
        formulario.add(new JLabel("Puerto"));
        formulario.add(portField);
        formulario.add(new JLabel("Token"));
        formulario.add(tokenField);
        formulario.add(new JLabel("Nombre"));
        formulario.add(nombreField);
        formulario.add(new JLabel("Sector"));
        formulario.add(sectorField);
        formulario.add(new JLabel("Nivel escolar"));
        formulario.add(nivelField);
        formulario.add(new JLabel("Latitud"));
        formulario.add(latField);
        formulario.add(new JLabel("Longitud"));
        formulario.add(lngField);

        JButton listarButton = new JButton("Listar por usuario");
        listarButton.addActionListener(e -> listar());

        JButton crearButton = new JButton("Crear formulario");
        crearButton.addActionListener(e -> crear());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(listarButton);
        acciones.add(crearButton);

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        frame.add(formulario, BorderLayout.NORTH);
        frame.add(acciones, BorderLayout.CENTER);
        frame.add(new JScrollPane(salida), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void listar() {
        try {
            salida.setText(service.listar(
                    hostField.getText().trim(),
                    Integer.parseInt(portField.getText().trim()),
                    tokenField.getText().trim()));
        } catch (Exception e) {
            salida.setText("Error: " + e.getMessage());
        }
    }

    private void crear() {
        try {
            salida.setText(service.crear(
                    hostField.getText().trim(),
                    Integer.parseInt(portField.getText().trim()),
                    tokenField.getText().trim(),
                    nombreField.getText().trim(),
                    sectorField.getText().trim(),
                    nivelField.getText().trim(),
                    Double.parseDouble(latField.getText().trim()),
                    Double.parseDouble(lngField.getText().trim()),
                    ""));
        } catch (Exception e) {
            salida.setText("Error: " + e.getMessage());
        }
    }
}
