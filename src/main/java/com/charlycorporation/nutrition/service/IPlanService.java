package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Plan;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;


public interface IPlanService {

    public Plan guardarPlan(Cliente cliente, String dietaJson, String dietaHtml);
    public List<Plan> listarPlanes();
    public void eliminarPlan(Long id);
    public Plan buscarPorId(Long id);
    Page<Plan> listarPaginado(int page, int size);
    public List<Plan> buscar(String q);
    public Plan actualizarPlan(Long id, Map<String, Object> payload);
}
