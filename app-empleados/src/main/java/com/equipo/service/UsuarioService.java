package com.equipo.service;

import com.equipo.dto.RegistroUsuarioDTO;
import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para registrar un nuevo usuario desde el DTO (con validación de contraseñas)
    public Usuario registrarUsuarioDesdeDTO(RegistroUsuarioDTO dto) throws Exception {

        // Verificamos si el email ya está registrado
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Ya existe un usuario con este correo: {}", dto.getEmail());
            throw new Exception("Ya existe un usuario con ese correo");
        }

        // Creamos el nuevo objeto Usuario con los datos del DTO
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Guardamos la contraseña cifrada

        // Guardamos el usuario en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado con éxito: {}", usuarioGuardado.getEmail());

        return usuarioGuardado;
    }

    // Método para crear un nuevo usuario
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Método para obtener un usuario por su ID
    public Optional<Usuario> obtenerUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    // Método para obtener un usuario por su email
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Método para eliminar un usuario por ID
    public void eliminarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

    // Método para actualizar los datos de un usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
