package com.ecommerce.catalogo.domain.model.gateway;

import java.math.BigDecimal;

public interface UsuarioGateway {

    boolean usuarioExiste(Long usuarioId);
    BigDecimal consultarSaldo(Long usuarioId);
    void descontarSaldo(Long usuarioId, BigDecimal monto);
    void reintegrarSaldo(Long usuarioId, BigDecimal monto);
}