package com.charlycorporation.nutrition.repository;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByClienteNombreContainingIgnoreCase(String nombre);
    long countByCliente(Cliente cliente);

    // 🔥 ESTE ES EL IMPORTANTE
    Optional<Plan> findTopByClienteOrderByFechaDesc(Cliente cliente);
}
