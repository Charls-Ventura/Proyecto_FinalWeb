package edu.pucmm.icc352.proyectofinalweb.client.rest;

import com.fasterxml.jackson.databind.JsonNode;
import edu.pucmm.icc352.proyectofinalweb.dto.FormularioDto;
import edu.pucmm.icc352.proyectofinalweb.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestClientService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String login(String baseUrl, String username, String password) throws IOException, InterruptedException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = JsonUtil.MAPPER.readTree(response.body());
        return json.path("token").asText();
    }

    public String listarMisFormularios(String baseUrl, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/formularios/mios"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public String crearFormulario(String baseUrl, String token, FormularioDto dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/formularios"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(dto)))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}