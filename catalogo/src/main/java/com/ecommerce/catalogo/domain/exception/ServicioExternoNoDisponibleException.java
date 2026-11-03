package com.ecommerce.catalogo.domain.exception;

public class ServicioExternoNoDisponibleException extends RuntimeException {
    public ServicioExternoNoDisponibleException(String servicio, Throwable causa) {
        super("El servicio " + servicio + " no está disponible", causa);
    }
}