package com.ecommerce.catalogo.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Producto {
    private long id;
    private String nombre;
    private  String descripcion;
    private double precio;
    private int stock;
    private String imagen;
}
