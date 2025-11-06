package com.project.finance_webflux.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        if (jwtUtil.validateToken(authToken)) {
            String username = jwtUtil.getUsernameFromToken(authToken);
            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, null));
        } else {
            return Mono.empty();
        }
    }
}