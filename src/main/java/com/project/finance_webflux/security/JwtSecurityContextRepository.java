package com.project.finance_webflux.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // No guardamos el contexto, JWT es stateless
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = null;

        // Leer token desde Authorization: Bearer
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Si no está en header, leer cookie "jwt"
        if (token == null) {
            var cookie = exchange.getRequest().getCookies().getFirst("jwt");
            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        // No hay token → no autenticado
        if (token == null) {
            return Mono.empty();
        }

        // ✅ Delega validación a AuthenticationManager
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);

        return authenticationManager.authenticate(auth)
                .map(SecurityContextImpl::new);
    }


}