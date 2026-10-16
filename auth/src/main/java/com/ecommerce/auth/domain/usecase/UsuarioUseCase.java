package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;
import com.ecommerce.auth.domain.model.gateway.UsuarioGateway;
import com.ecommerce.auth.domain.usecase.validation.UsuarioValidator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final EncrypterGateway encrypterGateway;

    public Usuario guardar(Usuario usuario) {
        UsuarioValidator.validar(usuario);

        String claveEncrypt = encrypterGateway.encrypt(usuario.getClave());
        usuario.setClave(claveEncrypt);
        usuario.setSaldo(BigDecimal.ZERO);
        return usuarioGateway.guardar(usuario);
    }

    public Usuario buscarPorId(Long id) {
        UsuarioValidator.validarId(id);
        return Optional.ofNullable(usuarioGateway.buscarPorId(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        UsuarioValidator.validarActualizacion(usuario);
        Usuario existente = buscarPorId(usuario.getIdUsuario());

        usuario.setClave(encrypterGateway.encrypt(usuario.getClave()));
        usuario.setSaldo(existente.getSaldo());

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

        if (encrypterGateway.checkPass(password, usuarioLogueo.getClave())) {
            return "Credenciales Validas";
        } else {
            return "Credenciales Invalidas revisar porfavor";
        }
    }
    public BigDecimal consultarSaldo(Long id) {
        return saldoDe(buscarPorId(id));
    }

    public Usuario recargarSaldo(Long id, BigDecimal monto) {
        UsuarioValidator.validarId(id);
        UsuarioValidator.validarMontoRecarga(monto);
        Usuario usuario = buscarPorId(id);
        return usuarioGateway.actualizarSaldo(id, saldoDe(usuario).add(monto));
    }

    public Usuario asignarSaldo(Long id, BigDecimal saldo) {
        UsuarioValidator.validarId(id);
        UsuarioValidator.validarSaldoAsignado(saldo);
        buscarPorId(id);
        return usuarioGateway.actualizarSaldo(id, saldo);
    }

    public Usuario descontarSaldo(Long id, BigDecimal monto) {
        UsuarioValidator.validarId(id);
        Usuario usuario = buscarPorId(id);
        BigDecimal saldoActual = saldoDe(usuario);
        UsuarioValidator.validarDescuento(saldoActual, monto);
        return usuarioGateway.actualizarSaldo(id, saldoActual.subtract(monto));
    }

    private BigDecimal saldoDe(Usuario usuario) {
        return Optional.ofNullable(usuario.getSaldo()).orElse(BigDecimal.ZERO);
    }



}