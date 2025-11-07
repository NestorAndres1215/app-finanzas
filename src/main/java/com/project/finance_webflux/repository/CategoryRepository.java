package com.project.finance_webflux.repository;

import com.project.finance_webflux.models.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {
    Mono<Category> findByNombre(String nombre);
}
