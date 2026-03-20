package com.isabella.controle_gastos.login;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // ✅ Chave vinda de variável de ambiente (nunca hardcoded)
    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "chave-padrao-dev-minimo-32-caracteres-ok" // só usado em dev local
    );

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token válido por 1 hora
    private static final long EXPIRATION_MS = 1000L * 60 * 60;

    public static String gerarToken(String usuario) {
        return Jwts.builder()
                .setSubject(usuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    public static String validarToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}