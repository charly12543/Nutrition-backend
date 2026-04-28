package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Usuario;
import com.charlycorporation.nutrition.repository.UsuarioRepository;
import com.charlycorporation.nutrition.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String usuario, String password){

        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!user.getPassword().equals(password)){
            throw new RuntimeException("Password incorrecto");
        }

        return jwtUtil.generateToken(user.getUsuario());
    }
}
