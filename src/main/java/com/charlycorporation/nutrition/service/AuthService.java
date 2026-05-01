package com.charlycorporation.nutrition.service;

import com.charlycorporation.nutrition.model.Usuario;
import com.charlycorporation.nutrition.repository.UsuarioRepository;
import com.charlycorporation.nutrition.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String usuario, String password){

        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Password incorrecto");
        }

        //return jwtUtil.generateToken(user.getUsuario());
        return jwtUtil.generateToken(user.getUsuario(), user.getRol());
    }
}
