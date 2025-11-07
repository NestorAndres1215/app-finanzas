package com.project.finance_webflux.repository;

import com.project.finance_webflux.models.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {

    Flux<Transaction> findByUsuarioId(Long usuarioId);

    Flux<Transaction> findByCategoriaId(Long categoriaId);
}