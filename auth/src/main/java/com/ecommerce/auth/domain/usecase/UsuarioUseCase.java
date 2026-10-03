package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.UsuarioGateway;
import com.ecommerce.auth.domain.usecase.validation.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    public Usuario guardar(Usuario usuario) {
        UsuarioValidator.validar(usuario);
        return usuarioGateway.guardar(usuario);
    }

    public Usuario buscarPorId(Long id) {
        UsuarioValidator.validarId(id);
        return Optional.ofNullable(usuarioGateway.buscarPorId(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        UsuarioValidator.validarActualizacion(usuario);
        buscarPorId(usuario.getIdUsuario());
        return usuarioGateway.actualizarUsuario(usuario);
    }

    public void eliminarPorId(Long id) {
        buscarPorId(id);
        usuarioGateway.eliminarPorId(id);
    }

    public Usuario buscarPorCorreo(String correo) {
        return Optional.ofNullable(usuarioGateway.buscarPorCorreo(correo))
                .orElseThrow(() -> new UsuarioNoEncontradoException(correo));
    }

    public String loginUsuario(String correo, String password) {
        Usuario usuarioLogueo = usuarioGateway.buscarPorCorreo(correo);

        if (usuarioLogueo == null) {
            return "Usuario no encontrado";
        }

        if (usuarioLogueo.getClave().equals(password)) {
            return "Credenciales Validas";
        } else {
            return "Credenciales Invalidas";
        }
    }


}