package com.ecommerce.catalogo.infraestructure.driver_adapter.transaction;

import com.ecommerce.catalogo.domain.model.gateway.TransaccionGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class TransaccionSpringAdapter implements TransaccionGateway {

    private final TransactionTemplate transactionTemplate;

    @Override
    public void ejecutar(Runnable accion) {
        transactionTemplate.executeWithoutResult(status -> accion.run());
    }
}