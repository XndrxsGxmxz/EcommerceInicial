package com.ecommerce.catalogo.domain.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(BigDecimal saldo, BigDecimal total) {
        super("Saldo insuficiente. Saldo: " + saldo + ", total de la compra: " + total);
    }

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}