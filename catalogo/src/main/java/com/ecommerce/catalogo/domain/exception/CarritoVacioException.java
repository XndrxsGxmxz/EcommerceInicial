package com.ecommerce.catalogo.domain.exception;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException(Long usuarioId) {
        super("El carrito del usuario " + usuarioId + " está vacío");
    }
}