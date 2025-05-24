package com.equipo.service;

import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioAdminService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminService.class);
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioAdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public Usuario bloquearUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (usuario.isCuentaBloqueada()) {
            logger.warn("Intento de bloquear cuenta que ya está bloqueada para el usuario: {}", usuario.getEmail());
            // Opcional: podrías lanzar una excepción o simplemente no hacer nada y devolver el usuario
            // throw new IllegalStateException("La cuenta ya está bloqueada."); //
            return usuario;
        }

        usuario.setCuentaBloqueada(true);
        usuario.setFechaBloqueo(LocalDateTime.now());

        logger.info("Cuenta del usuario {} bloqueada por un administrador.", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario desbloquearUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (!usuario.isCuentaBloqueada()) {
            logger.warn("Intento de desbloquear cuenta que no está bloqueada para el usuario: {}", usuario.getEmail());
            // Opcional: podrías lanzar una excepción o simplemente no hacer nada y devolver el usuario
            // throw new IllegalStateException("La cuenta no está bloqueada."); //
            return usuario;
        }

        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaBloqueo(null);
        logger.info("Cuenta del usuario {} desbloqueada por un administrador. Intentos fallidos reseteados.", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }
}