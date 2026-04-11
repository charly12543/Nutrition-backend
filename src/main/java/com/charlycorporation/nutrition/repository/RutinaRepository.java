package com.charlycorporation.nutrition.repository;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    long countByCliente(Cliente cliente);
    Optional<Rutina> findTopByClienteOrderByFechaDesc(Cliente cliente);
}
