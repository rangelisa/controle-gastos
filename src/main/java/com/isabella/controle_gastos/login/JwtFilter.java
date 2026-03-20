package com.isabella.controle_gastos.login;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class JwtFilter implements Filter {

    // ✅ Rotas públicas que não exigem token
    private static final List<String> ROTAS_PUBLICAS = List.of(
            "/auth/login",
            "/auth/cadastrar"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // ✅ Libera rotas públicas sem checar token
        boolean isPublica = ROTAS_PUBLICAS.stream().anyMatch(uri::startsWith);
        if (isPublica) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        // ✅ Bloqueia requisição se não houver token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Token ausente ou inválido");
            return;
        }

        String token = authHeader.substring(7);

        try {
            String usuario = JwtUtil.validarToken(token);
            httpRequest.setAttribute("usuario", usuario);
            chain.doFilter(request, response);

        } catch (Exception e) {
            // ✅ Bloqueia requisição se o token for inválido ou expirado
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Token inválido ou expirado");
        }
    }
}