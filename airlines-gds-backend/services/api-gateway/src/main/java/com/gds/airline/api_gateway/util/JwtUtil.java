package com.gds.airline.api_gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    // Extraemos la misma clave que usa el auth-service
    @Value("${jwt.secret}")
    private String secret;

    public void validateToken(final String token) {
        // Si el token es falso, ha caducado o está manipulado, esto lanzará una excepción
        Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
