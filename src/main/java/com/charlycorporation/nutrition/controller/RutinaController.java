package com.charlycorporation.nutrition.controller;

import com.charlycorporation.nutrition.model.Cliente;
import com.charlycorporation.nutrition.model.Rutina;
import com.charlycorporation.nutrition.service.RutinaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class RutinaController {

    private final RutinaService rutinaService;

    /* ===================== GUARDAR ===================== */
    @PostMapping("/guardar-rutina")
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> payload){

        try {
            ObjectMapper mapper = new ObjectMapper();

            // 🔥 VALIDAR CLIENTE
            Object clienteObj = payload.get("cliente");
            if(clienteObj == null){
                return ResponseEntity.badRequest().body("❌ cliente requerido");
            }

            Cliente cliente = mapper.convertValue(clienteObj, Cliente.class);

            // 🔥 HTML
            Object htmlObj = payload.get("rutinaHtml");
            if(htmlObj == null){
                return ResponseEntity.badRequest().body("❌ rutinaHtml requerido");
            }

            String html = htmlObj.toString();

            Rutina rutina = rutinaService.guardarRutina(cliente, html);

            return ResponseEntity.ok(rutina);

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error: " + e.getMessage());
        }
    }

    /* ===================== LISTAR ===================== */
    @GetMapping("/rutinas")
    public Page<Rutina> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return rutinaService.listarPaginado(page, size);
    }

    /* ===================== BUSCAR ===================== */
    @GetMapping("/rutinas/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id){
        try {
            Rutina r = rutinaService.buscarPorId(id);
            return ResponseEntity.ok(r);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    /* ===================== ELIMINAR ===================== */
    @DeleteMapping("/rutinas/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        rutinaService.eliminarRutina(id);
        return ResponseEntity.ok().build();
    }

    /* ===================== UPDATE ===================== */
    @PutMapping("/rutinas/{id}")
    public ResponseEntity<?> actualizarRutina(@PathVariable Long id, @RequestBody Map<String, Object> payload){

        try {

            Rutina r = rutinaService.actualizarRutina(id, payload);

            return ResponseEntity.ok(r);

        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



}