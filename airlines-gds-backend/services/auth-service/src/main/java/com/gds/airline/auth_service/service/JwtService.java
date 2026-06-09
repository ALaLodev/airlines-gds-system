package com.gds.airline.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Extraemos las variables directamente del Config Server (puerto 8888)
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Este metodo es el que genera el Token
     */
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return Jwts.builder()
                .claim("role", role) // Guardamos el rol dentro del token
                .subject(userDetails.getUsername()) // El 'subject' suele ser el email/usuario
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de creación (Ahora)
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de caducidad
                .signWith(getSignInKey()) // La firma criptográfica
                .compact(); // Construye y devuelve el String final (el token)
    }

    /**
     * Transforma nuestra palabra secreta en una clave criptográfica real
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extrae el email (subject) del token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Valida si el token es correcto y pertenece al usuario
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Comprueba si el token ha caducado
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Métodos de apoyo interno para leer las tripas del JWT
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Usamos la misma clave secreta para descifrar
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}