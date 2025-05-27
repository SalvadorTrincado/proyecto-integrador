package com.equipo.service;

import com.equipo.dto.RegistroUsuarioDTO;
import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Asegúrate de que esté importado

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

    /**
     * Actualiza la contraseña de un usuario dado su email.
     * @param email El email del usuario.
     * @param nuevaPassword La nueva contraseña sin encriptar.
     * @return true si la contraseña fue actualizada, false si el usuario no fue encontrado.
     */
    @Transactional
    public boolean actualizarPasswordPorEmail(String email, String nuevaPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            logger.info("Contraseña actualizada para el usuario: {}", email);
            return true;
        }
        logger.warn("Intento de actualizar contraseña para email no existente: {}", email);
        return false;
    }
}