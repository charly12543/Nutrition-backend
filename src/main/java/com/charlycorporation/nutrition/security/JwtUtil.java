package com.charlycorporation.nutrition.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "mi-super-secret-key-que-es-larga-y-segura-123456".getBytes()
    );

    public String generateToken(String usuario, String rol){

        return Jwts.builder()
                .setSubject(usuario)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .signWith(SECRET_KEY)
                .compact();
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsuario(String token){
        return getClaims(token).getSubject();
    }

    public String extractRol(String token){
        return getClaims(token).get("rol", String.class);
    }
}