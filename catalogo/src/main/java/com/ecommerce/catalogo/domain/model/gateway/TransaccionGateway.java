package com.ecommerce.catalogo.domain.model.gateway;

public interface TransaccionGateway {
    void ejecutar(Runnable accion);
}
