package com.ecommerce.catalogo.domain.usecase.validation;

import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.model.ItemCarrito;

import java.util.ArrayList;
import java.util.List;

public final class CarritoValidator {

    private CarritoValidator() {}

    public static void validarUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0)
            throw new DatosInvalidosException(List.of("El id del usuario debe ser un número positivo"));
    }

    public static void validarAgregar(Long usuarioId, ItemCarrito item) {
        List<String> errores = new ArrayList<>();

        if (usuarioId == null || usuarioId <= 0)
            errores.add("El id del usuario debe ser un número positivo");

        if (item == null) {
            errores.add("El item es obligatorio");
        } else {
            if (item.getProductoId() == null || item.getProductoId() <= 0)
                errores.add("El id del producto debe ser un número positivo");
            if (item.getCantidad() == null || item.getCantidad() <= 0)
                errores.add("La cantidad debe ser mayor que cero");
        }

        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);
    }

    public static void validarEliminar(Long usuarioId, Long productoId) {
        List<String> errores = new ArrayList<>();
        if (usuarioId == null || usuarioId <= 0)
            errores.add("El id del usuario debe ser un número positivo");
        if (productoId == null || productoId <= 0)
            errores.add("El id del producto debe ser un número positivo");
        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);
    }
}