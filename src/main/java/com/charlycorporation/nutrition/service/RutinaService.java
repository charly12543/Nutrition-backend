package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Rutina;
import com.charlycorporation.nutrition.repository.ClienteRepository;
import com.charlycorporation.nutrition.repository.PlanRepository;
import com.charlycorporation.nutrition.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RutinaService implements IRutinaService{

    private final RutinaRepository rutinaRepo;
    private final ClienteRepository clienteRepo;
    private final PlanRepository planRepo;

    public Rutina guardarRutina(Cliente cliente, String html){

        // 🔍 BUSCAR / CREAR CLIENTE
        Cliente clienteGuardado;

        if(cliente.getId() != null){

            clienteGuardado = clienteRepo.findById(cliente.getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            clienteGuardado.setPeso(cliente.getPeso());
            clienteGuardado.setAltura(cliente.getAltura());
            clienteGuardado.setEdad(cliente.getEdad());
            clienteGuardado.setSexo(cliente.getSexo());
            clienteGuardado.setObjetivo(cliente.getObjetivo());
            clienteGuardado.setNivel(cliente.getNivel());
            clienteGuardado.setDiasGym(cliente.getDiasGym());
            clienteGuardado.setTomaWhey(cliente.getTomaWhey());

        } else {

            cliente.setFechaRegistro(LocalDate.now());
            clienteGuardado = cliente;
        }

        clienteGuardado = clienteRepo.save(clienteGuardado);

        // 🔥 NUEVO: VALIDAR SI YA TIENE UNA RUTINA (EVITAR DUPLICADOS)
        Optional<Rutina> existente = rutinaRepo.findTopByClienteOrderByFechaDesc(clienteGuardado);

        if(existente.isPresent()){
            Rutina r = existente.get();

            r.setRutinaHtml(html);
            r.setFecha(LocalDate.now());

            return rutinaRepo.save(r);
        }

        // 🔥 CREAR NUEVA SI NO EXISTE
        Rutina r = new Rutina();
        r.setCliente(clienteGuardado);
        r.setRutinaHtml(html);
        r.setFecha(LocalDate.now());

        return rutinaRepo.save(r);
    }

    public List<Rutina> listarRutinas(){
        return rutinaRepo.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }

    @Override
    public Rutina buscarPorId(Long id){
        return rutinaRepo.findById(id).orElse(null);
    }

    @Override
    public void eliminarRutina(Long id){
        Rutina rutina = rutinaRepo.findById(id).orElseThrow();

        Cliente cliente = rutina.getCliente();

        rutinaRepo.delete(rutina);

        // 🔥 ELIMINAR CLIENTE SI YA NO TIENE NADA
        if(planRepo.countByCliente(cliente) == 0 &&
                rutinaRepo.countByCliente(cliente) == 0){

            clienteRepo.delete(cliente);
        }
    }

    @Override
    public Page<Rutina> listarPaginado(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return rutinaRepo.findAll(pageable);
    }

    @Override
    public Rutina actualizarRutina(Long id, Map<String, Object> payload){

        Rutina existente = rutinaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        Object htmlObj = payload.get("rutinaHtml");
        existente.setRutinaHtml(htmlObj != null ? htmlObj.toString() : "");

        existente.setFecha(LocalDate.now());

        return rutinaRepo.save(existente);
    }
}