package com.ecommerce.auth.infraestructure.driver_adapters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UsuarioDataJpaRepository extends JpaRepository<UsuarioData, Long> {

}

