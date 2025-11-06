package com.project.finance_webflux.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Mono<Void> handleAppException(ServerWebExchange exchange, AppException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        var buffer = exchange.getResponse().bufferFactory().wrap(ex.getMessage().getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @ExceptionHandler(Exception.class)
    public Mono<Void> handleGeneralException(ServerWebExchange exchange, Exception ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        var buffer = exchange.getResponse().bufferFactory().wrap(("Error interno: " + ex.getMessage()).getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}