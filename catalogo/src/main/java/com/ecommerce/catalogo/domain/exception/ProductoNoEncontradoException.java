package com.ecommerce.catalogo.domain.exception;

public class ProductoNoEncontradoException extends RecursoNoEncontradoException {
    public ProductoNoEncontradoException(Long id) {
        super("No se encontró el producto con id: " + id);
    }
}