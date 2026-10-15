package com.ecommerce.catalogo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Carrito {
    private long id;
    private long idUsuario;
    private List<ItemCarrito> items;
    private double preciototal;
}
