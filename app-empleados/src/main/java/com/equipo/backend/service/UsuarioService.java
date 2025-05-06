package com.equipo.backend.service;

import com.equipo.backend.dto.RegistroUsuarioRequestDTO; // Importamos el DTO correcto
import com.equipo.backend.entity.Usuario;
import com.equipo.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public String codificarContrasena(String clave) {
        return passwordEncoder.encode(clave);
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // Método modificado para usar RegistroUsuarioRequestDTO
    public void registerNewUser(RegistroUsuarioRequestDTO registroDto) {
        if (usuarioRepository.existsByEmail(registroDto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (!registroDto.isClavesCoincidentes()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDto.getEmail()); // Podemos usar el email como nombre por simplicidad
        usuario.setEmail(registroDto.getEmail());
        usuario.setClave(passwordEncoder.encode(registroDto.getClave()));
        usuario.setFechaCreacion(LocalDateTime.now()); // Asegúrate de que la entidad Usuario tenga este campo

        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}