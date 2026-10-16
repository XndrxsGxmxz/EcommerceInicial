package com.ecommerce.catalogo.domain.exception;

public class CarritoNoEncontradoException extends RecursoNoEncontradoException {
    public CarritoNoEncontradoException(Long usuarioId) {
        super("No se encontró un carrito activo para el usuario con id: " + usuarioId);
    }
}