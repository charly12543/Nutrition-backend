package com.charlycorporation.nutrition.controller;

import com.charlycorporation.nutrition.model.Usuario;
import com.charlycorporation.nutrition.model.UsuarioRequest;
import com.charlycorporation.nutrition.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔥 LISTAR USUARIOS
    @GetMapping("/usuarios")
    public List<Usuario> listar(){
        return usuarioRepo.findAll();
    }

    // 🔥 CREAR USUARIO
    @PostMapping("/usuarios")
    public Usuario crear(@RequestBody UsuarioRequest req){

        if(req.password == null || req.password.isEmpty()){
            throw new RuntimeException("Password requerido");
        }

        Usuario u = new Usuario();
        u.setUsuario(req.usuario);
        u.setPassword(passwordEncoder.encode(req.password));
        u.setRol(req.rol != null ? req.rol : "COACH");

        return usuarioRepo.save(u);
    }

    // 🔥 ELIMINAR
    @DeleteMapping("/usuarios/{id}")
    public void eliminar(@PathVariable Long id){
        usuarioRepo.deleteById(id);
    }
}