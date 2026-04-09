package com.charlycorporation.nutrition.controller;

import com.charlycorporation.nutrition.model.LoginRequest;
import com.charlycorporation.nutrition.model.Usuario;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){

        try{
            Usuario user = authService.login(req.getUsuario(), req.getPassword());

            return ResponseEntity.ok(Map.of(
                    "token", "fake-jwt-123", // luego lo mejoramos
                    "usuario", user.getUsuario(),
                    "rol", user.getRol()
            ));

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }





   /* @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){

        if(req.getUsuario().equals("charly") && req.getPassword().equals("1234")){
            return ResponseEntity.ok(Map.of("token","123456"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }*/
}
