package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Medidas;
import com.charlycorporation.nutrition.model.Plan;
import com.charlycorporation.nutrition.model.Plicometria;
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

    // Metodo Guardar Dieta paciente
    public Plan guardarPlan(Cliente cliente, String dietaJson, String dietaHtml){

        ObjectMapper mapper = new ObjectMapper();

        try {

            // 🔥 VALIDACIÓN BÁSICA
            if(dietaJson == null || dietaJson.isEmpty()){
                throw new RuntimeException("dietaJson viene vacío");
            }

            JsonNode root = mapper.readTree(dietaJson);

            // 🔥 VALIDACIÓN PRO (más segura)
            if(!root.hasNonNull("calorias") || !root.hasNonNull("proteina")){
                throw new RuntimeException("JSON incompleto: " + dietaJson);
            }

            // 🔥 EXTRAER DATOS
            String caloriasStr = root.path("calorias").asText("0");
            String proteinaStr = root.path("proteina").asText("0");
            String carbosStr = root.path("carbos").asText("0");
            String grasaStr = root.path("grasa").asText("0");

            // 🔥 EXTRA NUEVO (por si lo usas)
            double grasaCorporal = root.path("grasaCorporal").asDouble(0);

            int fase = 1;
            if(root.has("clienteInfo") && root.get("clienteInfo").has("fase")){
                fase = root.get("clienteInfo").get("fase").asInt();
            }

            // 🔥 PARSE SAFE
            int calorias = parseSafe(caloriasStr);
            int proteina = parseSafe(proteinaStr);
            int carbos = parseSafe(carbosStr);
            int grasa = parseSafe(grasaStr);

            // ===================== CLIENTE =====================
            Cliente clienteGuardado;

            if(cliente.getId() != null && clienteRepo.existsById(cliente.getId())){

                clienteGuardado = clienteRepo.findById(cliente.getId())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

                // 🔥 UPDATE
                clienteGuardado.setPeso(cliente.getPeso());
                clienteGuardado.setAltura(cliente.getAltura());
                clienteGuardado.setEdad(cliente.getEdad());
                clienteGuardado.setSexo(cliente.getSexo());
                clienteGuardado.setObjetivo(cliente.getObjetivo());
                clienteGuardado.setNivel(cliente.getNivel());
                clienteGuardado.setDiasGym(cliente.getDiasGym());
                clienteGuardado.setTomaWhey(cliente.getTomaWhey());

                // 🔥 MEDIDAS
                if(cliente.getMedidas() != null){

                    if(clienteGuardado.getMedidas() == null){
                        clienteGuardado.setMedidas(new Medidas());
                    }

                    Medidas m = cliente.getMedidas();
                    Medidas mg = clienteGuardado.getMedidas();

                    mg.setBrazoIzquierdo(m.getBrazoIzquierdo());
                    mg.setBrazoDerecho(m.getBrazoDerecho());
                    mg.setCintura(m.getCintura());
                    mg.setPecho(m.getPecho());
                    mg.setEspalda(m.getEspalda());
                    mg.setPiernaIzquierda(m.getPiernaIzquierda());
                    mg.setPiernaDerecha(m.getPiernaDerecha());
                    mg.setPantorrillaIzquierda(m.getPantorrillaIzquierda());
                    mg.setPantorrillaDerecha(m.getPantorrillaDerecha());
                }

                // 🔥 PLICOMETRÍA
                if(cliente.getPlicometria() != null){

                    if(clienteGuardado.getPlicometria() == null){
                        clienteGuardado.setPlicometria(new Plicometria());
                    }

                    Plicometria p = cliente.getPlicometria();
                    Plicometria pg = clienteGuardado.getPlicometria();

                    pg.setBiceps(p.getBiceps());
                    pg.setTriceps(p.getTriceps());
                    pg.setSubescapular(p.getSubescapular());
                    pg.setSuprailiaco(p.getSuprailiaco());
                }

            } else {

                cliente.setFechaRegistro(LocalDate.now());

                if(cliente.getMedidas() == null){
                    cliente.setMedidas(new Medidas());
                }

                if(cliente.getPlicometria() == null){
                    cliente.setPlicometria(new Plicometria());
                }

                clienteGuardado = cliente;
            }

            clienteGuardado = clienteRepo.save(clienteGuardado);

            // ===================== PLAN =====================

            Optional<Plan> existente = planRepo.findTopByClienteOrderByFechaDesc(clienteGuardado);

            if(existente.isPresent()){
                Plan plan = existente.get();

                plan.setDieta(dietaJson);
                plan.setHtml(dietaHtml != null ? dietaHtml : "");

                plan.setCalorias(calorias);
                plan.setProteina(proteina);
                plan.setCarbos(carbos);
                plan.setGrasa(grasa);
                plan.setFase(fase);

                // 🔥 opcional si tienes campo
                // plan.setGrasaCorporal(grasaCorporal);

                plan.setFecha(LocalDate.now());

                return planRepo.save(plan);
            }

            // 🔥 NUEVO PLAN
            Plan plan = new Plan();
            plan.setCliente(clienteGuardado);
            plan.setDieta(dietaJson);
            plan.setHtml(dietaHtml != null ? dietaHtml : "");

            plan.setCalorias(calorias);
            plan.setProteina(proteina);
            plan.setCarbos(carbos);
            plan.setGrasa(grasa);
            plan.setFase(fase);

            // 🔥 opcional
            // plan.setGrasaCorporal(grasaCorporal);

            plan.setFecha(LocalDate.now());

            return planRepo.save(plan);

        } catch (Exception e) {

            System.out.println("❌ ERROR GUARDAR PLAN:");
            e.printStackTrace();

            throw new RuntimeException("Error procesando dieta JSON: " + e.getMessage(), e);
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

                // 🔥 NUEVO (IMPORTANTE)
                if(clienteActualizado.getMedidas() != null){

                    if(cliente.getMedidas() == null){
                        cliente.setMedidas(new Medidas());
                    }

                    Medidas m = clienteActualizado.getMedidas();
                    Medidas mg = cliente.getMedidas();

                    mg.setBrazoIzquierdo(m.getBrazoIzquierdo());
                    mg.setBrazoDerecho(m.getBrazoDerecho());
                    mg.setCintura(m.getCintura());
                    mg.setPecho(m.getPecho());
                    mg.setEspalda(m.getEspalda());
                    mg.setPiernaIzquierda(m.getPiernaIzquierda());
                    mg.setPiernaDerecha(m.getPiernaDerecha());
                    mg.setPantorrillaIzquierda(m.getPantorrillaIzquierda());
                    mg.setPantorrillaDerecha(m.getPantorrillaDerecha());
                }

                if(clienteActualizado.getPlicometria() != null){

                    if(cliente.getPlicometria() == null){
                        cliente.setPlicometria(new Plicometria());
                    }

                    Plicometria p = clienteActualizado.getPlicometria(); // 🔥 origen (nuevo)
                    Plicometria pg = cliente.getPlicometria();            // 🔥 destino (persistente)

                    pg.setBiceps(p.getBiceps());
                    pg.setTriceps(p.getTriceps());
                    pg.setSubescapular(p.getSubescapular());
                    pg.setSuprailiaco(p.getSuprailiaco());
                }

                clienteRepo.save(cliente);
            }

            // 🔹 DIETA
            Object dietaObj = payload.get("dieta");
            String dietaJson = mapper.writeValueAsString(dietaObj);

            JsonNode root = mapper.readTree(dietaJson);

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
