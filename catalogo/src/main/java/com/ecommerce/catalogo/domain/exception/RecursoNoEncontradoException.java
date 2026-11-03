package com.ecommerce.catalogo.domain.exception;

public abstract class RecursoNoEncontradoException extends RuntimeException {
    protected RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}