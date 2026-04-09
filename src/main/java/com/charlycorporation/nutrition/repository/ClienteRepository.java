package com.charlycorporation.nutrition.repository;

import com.charlycorporation.nutrition.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByNombre(String nombre);
    Optional<Cliente> findByNombreAndEdadAndPeso(String nombre, int edad, double peso);
}
