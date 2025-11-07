package com.project.finance_webflux.service;


import com.project.finance_webflux.dto.LoginRequest;
import com.project.finance_webflux.dto.RegisterRequest;
import com.project.finance_webflux.dto.UpdateUserRequest;
import com.project.finance_webflux.exception.AppException;
import com.project.finance_webflux.models.User;
import com.project.finance_webflux.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.project.finance_webflux.util.Mensaje.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Registro de usuario
    public Mono<Object> registerUser(RegisterRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(existing -> Mono.error(new AppException(USERNAME_EXISTE)))
                .switchIfEmpty(
                        userRepository.findByEmail(request.getEmail())
                                .flatMap(existing -> Mono.error(new AppException(EMAIL_EXISTE)))
                )
                .switchIfEmpty(
                        Mono.defer(() -> {
                            User user = User.builder()
                                    .nombre(request.getNombre())
                                    .username(request.getUsername())
                                    .email(request.getEmail())
                                    .telefono(request.getTelefono())
                                    .password(passwordEncoder.encode(request.getPassword()))
                                    .estado(true)
                                    .rol(ROL_USER)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            return userRepository.save(user);
                        })
                );
    }

    // Login de usuario
    public Mono<User> loginUser(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new AppException(LOGIN_PASSWORD_INCORRECTO));
                    }
                })
                .switchIfEmpty(Mono.error(new AppException(LOGIN_USUARIO_NO_ENCONTRADO)));
    }

    public Flux<User> listarUsuarios() {
        return userRepository.findAll();
    }

    public Mono<User> listarPorId(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(USERNAME_NO_ENCONTRADO)));
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Mono<User> listarPorEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new AppException(USERNAME_NO_ENCONTRADO)));
    }

    public Mono<User> listarPorTelefono(String telefono) {
        return userRepository.findByTelefono(telefono)
                .switchIfEmpty(Mono.error(new AppException(USERNAME_NO_ENCONTRADO)));
    }

}