package com.charlycorporation.nutrition.controller;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Plan;
import com.charlycorporation.nutrition.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class PlanController {


    private final PlanService planService;

    @PostMapping("/guardar-plan")
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> payload){

        System.out.println("🔥 PAYLOAD: " + payload);

        try {

            ObjectMapper mapper = new ObjectMapper();

            // 🔹 VALIDAR CLIENTE
            Object clienteObj = payload.get("cliente");
            if(clienteObj == null){
                return ResponseEntity.badRequest().body("❌ cliente es null");
            }

            Cliente cliente = mapper.convertValue(clienteObj, Cliente.class);

            // 🔹 VALIDAR DIETA
            Object dietaObj = payload.get("dieta");
            if(dietaObj == null){
                return ResponseEntity.badRequest().body("❌ dieta es null");
            }

            String dietaJson = mapper.writeValueAsString(dietaObj);

            // 🔹 VALIDAR HTML
            Object htmlObj = payload.get("html");
            if(htmlObj == null){
                return ResponseEntity.badRequest().body("❌ html es null");
            }

            String dietaHtml = htmlObj.toString();
            Plan plan = planService.guardarPlan(cliente, dietaJson, dietaHtml);

            return ResponseEntity.ok(plan);

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/planes")
    public Page<Plan> listarPlanes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return planService.listarPaginado(page, size);
    }

    @GetMapping("/planes/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id){
        try {
            Plan p = planService.buscarPorId(id);
            return ResponseEntity.ok(p);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/planes/buscar")
    public List<Plan> buscar(@RequestParam String q){
        return planService.buscar(q);
    }




    @DeleteMapping("/planes/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        planService.eliminarPlan(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/planes/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload){

        try {

            Plan plan = planService.actualizarPlan(id, payload);

            return ResponseEntity.ok(plan);

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error interno: " + e.getMessage());
        }
    }


    @GetMapping("/health")
    public String health() {
        return "ok";
    }



}
