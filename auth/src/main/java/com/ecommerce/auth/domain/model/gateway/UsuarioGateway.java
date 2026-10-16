package com.ecommerce.auth.domain.model.gateway;

import com.ecommerce.auth.domain.model.Usuario;

import java.math.BigDecimal;


public interface UsuarioGateway {
    Usuario guardar(Usuario usuario);

    void eliminarPorId(Long idUsuario);

    Usuario buscarPorId(Long idUsuario);

    Usuario actualizarUsuario(Usuario usuario);

    Usuario buscarPorCorreo(String correo);

    Usuario actualizarSaldo(Long idUsuario, BigDecimal saldo);
}