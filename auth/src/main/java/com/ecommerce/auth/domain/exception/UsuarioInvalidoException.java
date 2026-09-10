package com.ecommerce.auth.domain.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class UsuarioInvalidoException extends RuntimeException {

    private final List<String> errores;

    public UsuarioInvalidoException(List<String> errores) {
        super(String.join(" | ", errores));
        this.errores = errores;
    }

}