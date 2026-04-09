package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Rutina;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface IRutinaService {

    public Rutina guardarRutina(Cliente cliente, String html);
    public List<Rutina> listarRutinas();
    public Rutina buscarPorId(Long id);
    public void eliminarRutina(Long id);
    public Page<Rutina> listarPaginado(int page, int size);
    public Rutina actualizarRutina(Long id, Map<String, Object> payload);

}
