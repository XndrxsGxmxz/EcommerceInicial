package com.ecommerce.catalogo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrito {
    private long id;
    private long carritoid;
    private long productoId;
    private String nombreProducto;
    private double precioUnitario;
    private int cantidad;
    private double subtotal;
}
