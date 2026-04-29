package com.charlycorporation.nutrition.controller;

import com.charlycorporation.nutrition.model.LoginRequest;
import com.charlycorporation.nutrition.model.Usuario;
import com.charlycorporation.nutrition.repository.UsuarioRepository;
import com.charlycorporation.nutrition.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    private final UsuarioRepository usuarioRepo;

    public AuthController(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){

        System.out.println("🔥 LOGIN INTENTO: " + req.getUsuario());

        try{
            String token = authService.login(req.getUsuario(), req.getPassword());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "usuario", req.getUsuario()
            ));

        }catch(Exception e){

            System.out.println("❌ ERROR LOGIN: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/init")
    public String init(){

        Usuario u = usuarioRepo.findByUsuario("admin")
                .orElse(new Usuario());

        u.setUsuario("admin");
        u.setPassword("1234"); // 🔥 FORZAR
        u.setRol("ADMIN");

        usuarioRepo.save(u);

        return "usuario actualizado";
    }



}
