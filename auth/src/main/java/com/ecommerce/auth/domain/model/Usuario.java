package com.ecommerce.auth.domain.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Usuario {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private String clave;
    private String rol;
    private String numeroTelefonico;
    private Integer edad;
}
