package com.ecommerce.catalogo.domain.exception;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super("Stock insuficiente para '" + producto + "'. Disponible: " + disponible + ", solicitado: " + solicitado);
    }
}