package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Plan;
import com.charlycorporation.nutrition.repository.ClienteRepository;
import com.charlycorporation.nutrition.repository.PlanRepository;
import com.charlycorporation.nutrition.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService implements IPlanService{

    private final ClienteRepository clienteRepo;

    private final PlanRepository planRepo;

    private final RutinaRepository rutinaRepo;

    //Metodo Guardar Dieta paciente
    public Plan guardarPlan(Cliente cliente, String dietaJson, String dietaHtml){

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(dietaJson);

            // 🔥 EXTRAER DATOS
         /*   String caloriasStr = root.path("calorias").asText();
            String proteinaStr = root.path("proteina").asText();
            String carbosStr = root.path("carbos").asText();
            String grasaStr = root.path("grasa").asText();
            int fase = root.path("clienteInfo").path("fase").asInt(); */

            String caloriasStr = root.has("calorias") ? root.get("calorias").asText() : "0";
            String proteinaStr = root.has("proteina") ? root.get("proteina").asText() : "0";
            String carbosStr = root.has("carbos") ? root.get("carbos").asText() : "0";
            String grasaStr = root.has("grasa") ? root.get("grasa").asText() : "0";

            int fase = root.has("clienteInfo") && root.get("clienteInfo").has("fase")
                    ? root.get("clienteInfo").get("fase").asInt()
                    : 1;

            // 🔥 LIMPIAR
           // int calorias = Integer.parseInt(caloriasStr.replaceAll("[^0-9]", ""));
            // int proteina = Integer.parseInt(proteinaStr.replaceAll("[^0-9]", ""));
           // int carbos = Integer.parseInt(carbosStr.replaceAll("[^0-9]", ""));
           // int grasa = Integer.parseInt(grasaStr.replaceAll("[^0-9]", ""));

            int calorias = parseSafe(caloriasStr);
            int proteina = parseSafe(proteinaStr);
            int carbos = parseSafe(carbosStr);
            int grasa = parseSafe(grasaStr);

            // 🔍 BUSCAR SI YA EXISTE CLIENTE
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

            // 🔥 NUEVO: VALIDAR SI YA TIENE UN PLAN (EVITAR DUPLICADOS)
            Optional<Plan> existente = planRepo.findTopByClienteOrderByFechaDesc(clienteGuardado);

            if(existente.isPresent()){
                Plan plan = existente.get();

                plan.setDieta(dietaJson);
                plan.setHtml(dietaHtml);

                plan.setCalorias(calorias);
                plan.setProteina(proteina);
                plan.setCarbos(carbos);
                plan.setGrasa(grasa);
                plan.setFase(fase);

                plan.setFecha(LocalDate.now());

                return planRepo.save(plan);
            }

            // 🔥 CREAR PLAN NUEVO (SI NO EXISTE)
            Plan plan = new Plan();
            plan.setCliente(clienteGuardado);
            plan.setDieta(dietaJson);
            plan.setHtml(dietaHtml);

            plan.setCalorias(calorias);
            plan.setProteina(proteina);
            plan.setCarbos(carbos);
            plan.setGrasa(grasa);
            plan.setFase(fase);

            plan.setFecha(LocalDate.now());

            return planRepo.save(plan);

        } catch (Exception e) {
            throw new RuntimeException("Error procesando dieta JSON", e);
        }
    }

    //Metodo lista de los pacientes registrados
    public List<Plan> listarPlanes() {
        return planRepo.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
    }


    @Override
    public void eliminarPlan(Long id) {
        Plan plan = planRepo.findById(id).orElseThrow();

        Cliente cliente = plan.getCliente();

        planRepo.delete(plan);

        // 🔥 validar si ya no tiene nada
        if(planRepo.countByCliente(cliente) == 0 &&
                rutinaRepo.countByCliente(cliente) == 0){

            clienteRepo.delete(cliente);
        }

    }

    @Override
    public Plan buscarPorId(Long id) {
        return planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
    }

    @Override
    public Page<Plan> listarPaginado(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return planRepo.findAll(pageable);
    }

    @Override
    public List<Plan> buscar(String q){
        try {
            Long id = Long.parseLong(q);
            try {
                return List.of(buscarPorId(id));
            } catch (Exception e){
                return List.of();
            }
        } catch (Exception e){
            return planRepo.findByClienteNombreContainingIgnoreCase(q);
        }
    }


    @Override
    public Plan actualizarPlan(Long id, Map<String, Object> payload){

        ObjectMapper mapper = new ObjectMapper();

        try {

            Plan existente = planRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

            Cliente cliente = existente.getCliente();

            // 🔹 CLIENTE
            Object clienteObj = payload.get("cliente");

            if(clienteObj != null){

                Cliente clienteActualizado = mapper.convertValue(clienteObj, Cliente.class);

                cliente.setEdad(clienteActualizado.getEdad());
                cliente.setPeso(clienteActualizado.getPeso());
                cliente.setAltura(clienteActualizado.getAltura());
                cliente.setSexo(clienteActualizado.getSexo());
                cliente.setObjetivo(clienteActualizado.getObjetivo());
                cliente.setNivel(clienteActualizado.getNivel());
                cliente.setDiasGym(clienteActualizado.getDiasGym());
                cliente.setTomaWhey(clienteActualizado.getTomaWhey());

                clienteRepo.save(cliente);
            }

            // 🔹 DIETA
            Object dietaObj = payload.get("dieta");
            String dietaJson = mapper.writeValueAsString(dietaObj);

            JsonNode root = mapper.readTree(dietaJson);

           /* String caloriasStr = root.path("calorias").asText();
            String proteinaStr = root.path("proteina").asText();
            String carbosStr = root.path("carbos").asText();
            String grasaStr = root.path("grasa").asText();
            int fase = root.path("clienteInfo").path("fase").asInt();

            int calorias = Integer.parseInt(caloriasStr.replaceAll("[^0-9]", ""));
            int proteina = Integer.parseInt(proteinaStr.replaceAll("[^0-9]", ""));
            int carbos = Integer.parseInt(carbosStr.replaceAll("[^0-9]", ""));
            int grasa = Integer.parseInt(grasaStr.replaceAll("[^0-9]", "")); */

            String caloriasStr = root.has("calorias") ? root.get("calorias").asText() : "0";
            String proteinaStr = root.has("proteina") ? root.get("proteina").asText() : "0";
            String carbosStr = root.has("carbos") ? root.get("carbos").asText() : "0";
            String grasaStr = root.has("grasa") ? root.get("grasa").asText() : "0";

            int fase = root.has("clienteInfo") && root.get("clienteInfo").has("fase")
                    ? root.get("clienteInfo").get("fase").asInt()
                    : 1;

            int calorias = parseSafe(caloriasStr);
            int proteina = parseSafe(proteinaStr);
            int carbos = parseSafe(carbosStr);
            int grasa = parseSafe(grasaStr);

            // 🔥 UPDATE PLAN
            existente.setDieta(dietaJson);

            Object htmlObj = payload.get("html");
            existente.setHtml(htmlObj != null ? htmlObj.toString() : "");
          //  existente.setHtml(payload.get("html").toString());

            existente.setCalorias(calorias);
            existente.setProteina(proteina);
            existente.setCarbos(carbos);
            existente.setGrasa(grasa);
            existente.setFase(fase);

            existente.setFecha(LocalDate.now());

            return planRepo.save(existente);

        } catch (Exception e){
            throw new RuntimeException("Error actualizando plan", e);
        }
    }


    private int parseSafe(String value){
        try {
            if(value == null) return 0;

            String limpio = value.replaceAll("[^0-9]", "");

            if(limpio.isEmpty()) return 0;

            return Integer.parseInt(limpio);

        } catch (Exception e){
            return 0;
        }
    }


}
