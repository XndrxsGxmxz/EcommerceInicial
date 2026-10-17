package com.ecommerce.catalogo.infraestructure.driver_adapter.jpa_repository.carrito;

import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.infraestructure.mapper.MapperCarrito;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CarritoDataGatewayImpl implements CarritoGateway {

    private final CarritoDataJpaRepository repository;
    private final MapperCarrito mapper;

    @Override
    public Carrito guardar(Carrito carrito) {
        var entity = mapper.toData(carrito);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Carrito buscarActivoPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioIdAndComprado(usuarioId, false)
                .map(mapper::toDomain)
                .orElse(null);
    }
}