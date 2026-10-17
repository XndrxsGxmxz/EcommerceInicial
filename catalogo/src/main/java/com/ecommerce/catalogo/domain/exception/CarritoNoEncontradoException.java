package com.ecommerce.catalogo.domain.exception;

public class CarritoNoEncontradoException extends RecursoNoEncontradoException {
    public CarritoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}