package com.ecommerce.auth.infraestructure.driver_adapters;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
@Data
public class UsuarioData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUsuario;
    private String nombre;
    @Column(length = 30, nullable = false, unique = true)
    private String correo;
    private String clave;
    private String rol;
    private Integer edad;
    private String numeroTelefonico;
    private BigDecimal saldo;

}
