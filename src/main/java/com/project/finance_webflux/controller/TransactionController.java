package com.project.finance_webflux.controller;

import com.project.finance_webflux.models.Transaction;
import com.project.finance_webflux.service.CategoryService;
import com.project.finance_webflux.service.TransactionService;
import com.project.finance_webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public Mono<String> listar(
            Authentication auth,
            Model model,
            @RequestParam(name = "filtro", required = false, defaultValue = "TODOS") String filtro) {

        String username = auth.getName();
        System.out.println("Usuario autenticado: " + username + ", filtro: " + filtro);

        return userService.findByUsername(username)
                .flatMap(user ->
                        transactionService.listarTransacciones()
                                .filter(tx -> tx.getUsuarioId().equals(user.getId()))
                                .collectList()
                                .flatMap(list ->
                                        categoryService.listarCategorias()
                                                .collectMap(cat -> cat.getId(), cat -> cat.getNombre())
                                                .map(categoryMap -> {

                                                    // Aplicar filtro
                                                    List<Transaction> filtradas = list.stream()
                                                            .filter(tx -> {
                                                                String catNombre = categoryMap.get(tx.getCategoriaId());
                                                                return filtro.equals("TODOS") || catNombre.equalsIgnoreCase(filtro);
                                                            })
                                                            .toList();

                                                    // Totales según filtradas
                                                    BigDecimal totalIngresos = filtradas.stream()
                                                            .filter(tx -> "INGRESO".equalsIgnoreCase(categoryMap.get(tx.getCategoriaId())))
                                                            .map(Transaction::getMonto)
                                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                                    BigDecimal totalEgresos = filtradas.stream()
                                                            .filter(tx -> "EGRESO".equalsIgnoreCase(categoryMap.get(tx.getCategoriaId())))
                                                            .map(Transaction::getMonto)
                                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                                    BigDecimal totalGeneral = totalIngresos.add(totalEgresos);
                                                    BigDecimal porcentajeIngresos = BigDecimal.ZERO;
                                                    BigDecimal porcentajeEgresos = BigDecimal.ZERO;

                                                    if (totalGeneral.compareTo(BigDecimal.ZERO) > 0) {
                                                        porcentajeIngresos = totalIngresos
                                                                .multiply(BigDecimal.valueOf(100))
                                                                .divide(totalGeneral, 2, BigDecimal.ROUND_HALF_UP);
                                                        porcentajeEgresos = totalEgresos
                                                                .multiply(BigDecimal.valueOf(100))
                                                                .divide(totalGeneral, 2, BigDecimal.ROUND_HALF_UP);
                                                    }

                                                    model.addAttribute("transactions", filtradas);
                                                    model.addAttribute("username", username);
                                                    model.addAttribute("totalIngresos", totalIngresos);
                                                    model.addAttribute("totalEgresos", totalEgresos);
                                                    model.addAttribute("porcentajeIngresos", porcentajeIngresos);
                                                    model.addAttribute("porcentajeEgresos", porcentajeEgresos);
                                                    model.addAttribute("filtro", filtro);
                                                    model.addAttribute("categoryMap", categoryMap);

                                                    return "transactions";
                                                })
                                )
                );
    }





    @GetMapping("/nuevo")
    public Mono<String> nuevo(Model model) {
        try {
            model.addAttribute("transaction", new Transaction());

            return categoryService.listarCategorias()
                    .collectList()  // Convertimos Flux → List para Thymeleaf
                    .flatMap(categorias -> {
                        model.addAttribute("categories", categorias);
                        return Mono.just("transaction-form");
                    })
                    .onErrorResume(e -> {
                        model.addAttribute("error", "Error cargando categorías: " + e.getMessage());
                        return Mono.just("transaction-form");
                    });

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
            return Mono.just("transaction-form");
        }
    }

    @PostMapping("/guardar")
    public Mono<String> guardar(@ModelAttribute Transaction transaction,
                                Authentication auth,
                                Model model) {

        return userService.findByUsername(auth.getName()) // buscar usuario por username
                .flatMap(user -> {
                    transaction.setUsuarioId(user.getId()); // ✅ ahora sí el ID correcto
                    return transactionService.crearTransaccion(transaction);
                })
                .map(saved -> "redirect:/transactions") // ✅ éxito
                .onErrorResume(e -> { // ✅ maneja error reactivo
                    model.addAttribute("error", "Error guardando transacción: " + e.getMessage());
                    model.addAttribute("transaction", transaction);
                    return categoryService.listarCategorias()
                            .collectList()
                            .map(categories -> {
                                model.addAttribute("categories", categories);
                                return "transaction-form"; // vuelve al form con error
                            });
                });
    }

    @GetMapping("/editar/{id}")
    public Mono<String> editar(@PathVariable Long id, Model model) {
        return transactionService.listarPorId(id)
                .flatMap(tx -> {
                    model.addAttribute("transaction", tx);

                    return categoryService.listarCategorias()
                            .collectList() // 🔥 IGUAL AQUÍ
                            .doOnNext(categories -> model.addAttribute("categories", categories))
                            .thenReturn("transaction-form");
                });
    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable Long id) {
        return transactionService.eliminarTransaccion(id)
                .thenReturn("redirect:/transactions");
    }

}