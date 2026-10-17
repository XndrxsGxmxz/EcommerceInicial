package com.ecommerce.catalogo.infraestructure.driver_adapter.jpa_repository.producto;

import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import com.ecommerce.catalogo.infraestructure.mapper.MapperProducto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductoDataGatewayImpl implements ProductoGateway {

    private final ProductoDataJpaRepository repository;
    private final MapperProducto mapper;


    @Override
    public Producto guardar(Producto producto) {
        return mapper.toProducto(repository.save(mapper.toData(producto)));
    }


    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toProducto);
    }


    @Override
    public List<Producto> obtenerTodos() {

        return repository.findAll().stream()
                .map(mapper::toProducto)
                .collect(Collectors.toList());
    }


    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}