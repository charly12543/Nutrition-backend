package com.charlycorporation.nutrition.repository;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    long countByCliente(Cliente cliente);
}
