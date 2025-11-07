package com.project.finance_webflux.service;

import com.project.finance_webflux.exception.AppException;
import com.project.finance_webflux.models.Transaction;
import com.project.finance_webflux.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.project.finance_webflux.util.Mensaje.TRANSACCION_NO_ENCONTRADA;


@Service
@RequiredArgsConstructor
public class TransactionService {


    private final TransactionRepository transactionRepository;

    // ✅ Crear transacción
    public Mono<Transaction> crearTransaccion(Transaction transaction) {
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    // ✅ Listar todas
    public Flux<Transaction> listarTransacciones() {
        return transactionRepository.findAll();
    }

    // ✅ Listar por ID
    public Mono<Transaction> listarPorId(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(TRANSACCION_NO_ENCONTRADA)));
    }

    // ✅ Listar por usuario
    public Flux<Transaction> listarPorUsuario(Long usuarioId) {
        return transactionRepository.findByUsuarioId(usuarioId);
    }

    // ✅ Listar por categoría
    public Flux<Transaction> listarPorCategoria(Long categoriaId) {
        return transactionRepository.findByCategoriaId(categoriaId);
    }

    // ✅ Actualizar transacción
    public Mono<Transaction> actualizarTransaccion(Long id, Transaction nuevaData) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(TRANSACCION_NO_ENCONTRADA)))
                .flatMap(existing -> {
                    existing.setUsuarioId(nuevaData.getUsuarioId());
                    existing.setCategoriaId(nuevaData.getCategoriaId());
                    existing.setMonto(nuevaData.getMonto());
                    existing.setDescripcion(nuevaData.getDescripcion());
                    return transactionRepository.save(existing);
                });
    }

    // ✅ Eliminar transacción
    public Mono<Void> eliminarTransaccion(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(TRANSACCION_NO_ENCONTRADA)))
                .flatMap(transactionRepository::delete);
    }
}