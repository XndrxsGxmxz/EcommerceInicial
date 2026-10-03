package com.ecommerce.auth.infraestructure.driver_adapters;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioDataJpaRepository extends JpaRepository<UsuarioData, Long> {
    Optional<UsuarioData> findByCorreo(String correo);
}

