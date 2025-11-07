package com.project.finance_webflux.service;

import com.project.finance_webflux.exception.AppException;
import com.project.finance_webflux.models.Category;
import com.project.finance_webflux.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.project.finance_webflux.util.Mensaje.*;

@RequiredArgsConstructor
@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    // ✅ Listar todas las categorías
    public Flux<Category> listarCategorias() {
        return categoryRepository.findAll();
    }

    // ✅ Buscar por ID
    public Mono<Category> listarPorId(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(CATEGORIA_NO_ENCONTRADA)));
    }

    // ✅ Buscar por nombre
    public Mono<Category> listarPorNombre(String nombre) {
        return categoryRepository.findByNombre(nombre)
                .switchIfEmpty(Mono.error(new AppException(CATEGORIA_NO_ENCONTRADA)));
    }

}
