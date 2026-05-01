package com.charlycorporation.nutrition.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 🔥 RUTAS PÚBLICAS (ESCALABLE)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/login",
            "/api/init",
            "/api/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ 1. PERMITIR PREFLIGHT (CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. VALIDAR SI ES RUTA PÚBLICA
        boolean isPublic = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);

        String authHeader = request.getHeader("Authorization");

        // ✅ 3. SI NO HAY TOKEN Y NO ES PÚBLICA → BLOQUEAR
        if ((authHeader == null || !authHeader.startsWith("Bearer ")) && !isPublic) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("❌ Token requerido");
            return;
        }

        // ✅ 4. SI HAY TOKEN → VALIDAR
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                String usuario = jwtUtil.extractUsuario(token);
                String rol = jwtUtil.extractRol(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                System.out.println("ROL DEL TOKEN: " + rol);


                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ Usuario autenticado: " + usuario);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("❌ Token inválido");
                return;
            }
        }



        // ✅ 5. CONTINUAR
        filterChain.doFilter(request, response);
    }
}