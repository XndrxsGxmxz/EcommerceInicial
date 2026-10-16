package com.ecommerce.catalogo.domain.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }
}