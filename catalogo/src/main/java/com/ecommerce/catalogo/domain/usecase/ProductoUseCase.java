package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.ProductoGateway;
import com.ecommerce.catalogo.domain.usecase.validation.ProductoValidator;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProductoUseCase {

    private final ProductoGateway productoGateway;

    public Producto guardarProducto(Producto producto) {
        ProductoValidator.validar(producto);
        return productoGateway.guardar(producto);
    }

    public Producto obtenerProducto(Long id) {
        ProductoValidator.validarId(id);
        return productoGateway.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public List<Producto> obtenerTodos() {
        return productoGateway.obtenerTodos();
    }

    public void eliminarProducto(Long id) {
        obtenerProducto(id);
        productoGateway.eliminar(id);
    }

    public Producto actualizarProducto(Long id, Producto producto) {
        ProductoValidator.validarId(id);
        ProductoValidator.validar(producto);
        obtenerProducto(id);
        producto.setId(id);
        return productoGateway.guardar(producto);
    }
}