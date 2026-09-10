package com.ecommerce.auth.domain.model.gateway;

import com.ecommerce.auth.domain.model.Usuario;


public interface UsuarioGateway {
    Usuario guardar(Usuario usuario);

    void eliminarPorId(Long idUsuario);

    Usuario buscarPorId(Long idUsuario);

    Usuario actualizarUsuario(Usuario usuario);

    Usuario buscarPorCorreo(String correo);
}