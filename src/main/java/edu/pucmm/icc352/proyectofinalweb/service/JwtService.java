package edu.pucmm.icc352.proyectofinalweb.service;

import edu.pucmm.icc352.proyectofinalweb.config.AppConfig;
import edu.pucmm.icc352.proyectofinalweb.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtService {
    private final SecretKey key = Keys.hmacShaKeyFor(AppConfig.jwtSecret().getBytes(StandardCharsets.UTF_8));

    public String generarToken(Usuario usuario) {
        long ahora = System.currentTimeMillis();
        long vence = ahora + (1000L * 60 * 60 * 12);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("rol", usuario.getRol().name())
                .issuedAt(new Date(ahora))
                .expiration(new Date(vence))
                .signWith(key)
                .compact();
    }

    public boolean esValido(String token) {
        try {
            leerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String usernameDesdeToken(String token) {
        return leerClaims(token).getSubject();
    }

    private Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
