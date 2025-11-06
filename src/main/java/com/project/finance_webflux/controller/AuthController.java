package com.project.finance_webflux.controller;


import com.project.finance_webflux.dto.LoginRequest;
import com.project.finance_webflux.dto.RegisterRequest;
import com.project.finance_webflux.models.User;
import com.project.finance_webflux.repository.UserRepository;
import com.project.finance_webflux.security.JwtUtil;
import com.project.finance_webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // Página de login
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Página de registro
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Página de registro
    @GetMapping("/registro")
    public String registerPage1() {
        return "registro";
    }
    // Registro de usuario
    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute RegisterRequest request, Model model) {
        return userService.registerUser(request)
                .map(user -> "redirect:/login")
                .onErrorResume(e -> {
                    model.addAttribute("error", e.getMessage());
                    return Mono.just("register");
                });
    }
    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@ModelAttribute LoginRequest request) {
        return userService.loginUser(request)
                .flatMap(user -> {
                    // Generar token
                    String token = jwtUtil.generateToken(user.getUsername());

                    // Crear cookie segura con el token
                    ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                            .httpOnly(true) // No accesible desde JS
                            .path("/")
                            .maxAge(60 * 60 * 24) // Expira en 1 día
                            .build();

                    // Redirigir a dashboard con cookie en la respuesta
                    return Mono.just(
                            ResponseEntity.status(302)
                                    .header("Location", "/dashboard")
                                    .header("Set-Cookie", jwtCookie.toString())
                                    .build()
                    );
                })
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(302)
                                .header("Location", "/login?error=true")
                                .build())
                );
    }


    // Login de usuario
    @GetMapping("/dashboard")
    public Mono<String> dashboard(@CookieValue(value = "jwt", required = false) String token, Model model) {

        if (token == null || !jwtUtil.validateToken(token)) {
            return Mono.just("redirect:/login");
        }

        String username = jwtUtil.extractUsername(token);

        return userRepository.findByUsername(username)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("username", username);
                    return "dashboard";
                })
                .switchIfEmpty(Mono.just("redirect:/login"));
    }



}