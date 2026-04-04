package edu.pucmm.icc352.proyectofinalweb.client.rest;

import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;

import javax.swing.*;
import java.awt.*;

public class RestDesktopClient {
    private final RestClientService service = new RestClientService();

    private final JTextField baseUrlField = new JTextField("http://localhost:7000");
    private final JTextField usernameField = new JTextField("admin");
    private final JPasswordField passwordField = new JPasswordField("admin123");
    private final JTextField tokenField = new JTextField();
    private final JTextField nombreField = new JTextField("Cliente REST");
    private final JTextField sectorField = new JTextField("Santiago");
    private final JTextField nivelField = new JTextField("Grado Universitario");
    private final JTextField latField = new JTextField("19.4517");
    private final JTextField lngField = new JTextField("-70.6970");
    private final JTextArea salida = new JTextArea();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RestDesktopClient().mostrar());
    }

    private void mostrar() {
        JFrame frame = new JFrame("Cliente REST - Proyecto Final Web");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Base URL"));
        formulario.add(baseUrlField);
        formulario.add(new JLabel("Usuario"));
        formulario.add(usernameField);
        formulario.add(new JLabel("Clave"));
        formulario.add(passwordField);
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

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> hacerLogin());

        JButton listarButton = new JButton("Listar");
        listarButton.addActionListener(e -> listar());

        JButton crearButton = new JButton("Crear formulario");
        crearButton.addActionListener(e -> crear());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(loginButton);
        acciones.add(listarButton);
        acciones.add(crearButton);

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        frame.add(formulario, BorderLayout.NORTH);
        frame.add(acciones, BorderLayout.CENTER);
        frame.add(new JScrollPane(salida), BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void hacerLogin() {
        try {
            String token = service.login(baseUrlField.getText().trim(), usernameField.getText().trim(),
                    new String(passwordField.getPassword()));
            tokenField.setText(token);
            salida.setText("Token generado correctamente");
        } catch (Exception e) {
            salida.setText("Error: " + e.getMessage());
        }
    }

    private void listar() {
        try {
            salida.setText(service.listarMisFormularios(baseUrlField.getText().trim(), tokenField.getText().trim()));
        } catch (Exception e) {
            salida.setText("Error: " + e.getMessage());
        }
    }

    private void crear() {
        try {
            FormularioDto dto = new FormularioDto();
            dto.setNombre(nombreField.getText().trim());
            dto.setSector(sectorField.getText().trim());
            dto.setNivelEscolar(nivelField.getText().trim());
            dto.setLatitud(Double.parseDouble(latField.getText().trim()));
            dto.setLongitud(Double.parseDouble(lngField.getText().trim()));
            dto.setFotoBase64("");

            salida.setText(service.crearFormulario(baseUrlField.getText().trim(), tokenField.getText().trim(), dto));
        } catch (Exception e) {
            salida.setText("Error: " + e.getMessage());
        }
    }
}
