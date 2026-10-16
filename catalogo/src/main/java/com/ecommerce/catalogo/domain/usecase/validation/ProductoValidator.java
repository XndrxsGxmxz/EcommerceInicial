package com.ecommerce.catalogo.domain.usecase.validation;

import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.model.Producto;

import java.util.ArrayList;
import java.util.List;

public final class ProductoValidator {

    private ProductoValidator() {}

    public static void validar(Producto producto) {
        if (producto == null) {
            throw new DatosInvalidosException(List.of("El producto es obligatorio"));
        }
        List<String> errores = new ArrayList<>();

        if (producto.getNombre() == null || producto.getNombre().isBlank())
            errores.add("El nombre es obligatorio");
        if (producto.getPrecio() == null || producto.getPrecio() <= 0)
            errores.add("El precio es obligatorio y debe ser mayor que cero");
        if (producto.getStock() == null || producto.getStock() < 0)
            errores.add("El stock es obligatorio y no puede ser negativo");

        if (!errores.isEmpty()) throw new DatosInvalidosException(errores);
    }

    public static void validarId(Long id) {
        if (id == null || id <= 0)
            throw new DatosInvalidosException(List.of("El id del producto debe ser un número positivo"));
    }
}