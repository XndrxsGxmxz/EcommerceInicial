package com.ecommerce.catalogo.infraestructure.driver_adapter.jpa_repository.carrito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoDataJpaRepository extends JpaRepository<CarritoData, Long> {

    Optional<CarritoData> findByUsuarioIdAndComprado(Long usuarioId, boolean comprado);
}