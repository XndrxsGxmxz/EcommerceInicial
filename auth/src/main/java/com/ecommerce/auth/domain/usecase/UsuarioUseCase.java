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

    // ---------- CRUD general (no permite fijar ni modificar el saldo) ----------

    public Usuario guardar(Usuario usuario) {
        UsuarioValidator.validar(usuario);          // rechaza saldo distinto de 0

        String claveEncrypt = encrypterGateway.encrypt(usuario.getClave());
        usuario.setClave(claveEncrypt);
        usuario.setSaldo(BigDecimal.ZERO);          // saldo inicial $0

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
        usuario.setSaldo(existente.getSaldo());     // el CRUD conserva el saldo actual

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

    // ---------- Operaciones de saldo (con controles de negocio) ----------

    public BigDecimal consultarSaldo(Long idUsuario) {
        return buscarPorId(idUsuario).getSaldo();
    }

    /** Operación autorizada: asigna un saldo específico (ej. saldo inicial distinto de 0). */
    public Usuario asignarSaldo(Long idUsuario, BigDecimal saldo) {
        UsuarioValidator.validarSaldoAsignado(saldo);
        buscarPorId(idUsuario);
        return usuarioGateway.actualizarSaldo(idUsuario, saldo);
    }

    /** Recarga: suma el monto al saldo actual. */
    public Usuario recargarSaldo(Long idUsuario, BigDecimal monto) {
        UsuarioValidator.validarMontoRecarga(monto);
        Usuario existente = buscarPorId(idUsuario);
        return usuarioGateway.actualizarSaldo(idUsuario, existente.getSaldo().add(monto));
    }

    /** Descuento: SOLO debe invocarse al confirmar una compra válida. */
    public Usuario descontarSaldoPorCompra(Long idUsuario, BigDecimal monto) {
        Usuario existente = buscarPorId(idUsuario);
        UsuarioValidator.validarDescuento(existente.getSaldo(), monto);
        return usuarioGateway.actualizarSaldo(idUsuario, existente.getSaldo().subtract(monto));
    }
}