package com.equipo.service;

import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException; // Para un error más específico
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

@Service
public class AutenticacionService implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionService.class);
    public static final int MAX_INTENTOS_FALLIDOS = 3; // Límite de intentos fallidos [cite: 105]
    public static final long DURACION_BLOQUEO_MINUTOS = 5; // Duración del bloqueo en minutos

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AutenticacionService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, LockedException {
        logger.debug("Intentando cargar usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con email: {}", email);
                    // Lanzamos BadCredentialsException para que el failureHandler lo trate como fallo de credenciales
                    // y no revele si el usuario existe o no directamente desde aquí.
                    // El failureHandler puede luego llamar a registrarIntentoFallido si es necesario.
                    throw new BadCredentialsException("Usuario no encontrado o credenciales inválidas");
                });

        if (usuario.isCuentaBloqueada()) {
            if (usuario.getFechaBloqueo() != null &&
                    LocalDateTime.now().isBefore(usuario.getFechaBloqueo().plusMinutes(DURACION_BLOQUEO_MINUTOS))) {
                long minutosRestantes = ChronoUnit.MINUTES.between(LocalDateTime.now(), usuario.getFechaBloqueo().plusMinutes(DURACION_BLOQUEO_MINUTOS)) + 1;
                logger.warn("Intento de acceso a cuenta bloqueada para el usuario: {}. Bloqueo activo por {} minutos más.", email, minutosRestantes);
                throw new LockedException("La cuenta de usuario está bloqueada. Inténtalo de nuevo en aproximadamente " + minutosRestantes + " minuto(s).");
            } else {
                // El tiempo de bloqueo ha expirado, desbloquear la cuenta
                logger.info("Tiempo de bloqueo expirado para el usuario: {}. Desbloqueando cuenta.", email);
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0); // Resetear intentos también
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            }
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                !usuario.isCuentaBloqueada(), // accountNonLocked
                Collections.emptyList()
        );
    }

    @Transactional
    public void registrarIntentoFallido(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            if (usuario.isCuentaBloqueada()) {
                // Si ya está bloqueada y el tiempo no ha pasado, loadUserByUsername lanzará LockedException.
                // No deberíamos llegar aquí si está activamente bloqueada, a menos que sea un reintento concurrente.
                logger.warn("Intento fallido en cuenta ya bloqueada (pero quizás expirando) para el usuario: {}", email);
                // No volvemos a incrementar si la lógica de expiración la maneja loadUserByUsername
                return;
            }

            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            logger.info("Intento fallido #{} registrado para {}", usuario.getIntentosFallidos(), email);

            if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                logger.warn("BLOQUEANDO cuenta para el usuario {} debido a {} intentos fallidos. Bloqueo por {} minutos.", email, MAX_INTENTOS_FALLIDOS, DURACION_BLOQUEO_MINUTOS);
                usuario.setCuentaBloqueada(true);
                usuario.setFechaBloqueo(LocalDateTime.now());
            }
            usuarioRepository.save(usuario);
        });
    }

    @Transactional
    public void registrarIntentoExitoso(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            if (usuario.getIntentosFallidos() > 0 || usuario.isCuentaBloqueada()) {
                logger.info("Reseteando intentos fallidos y desbloqueando cuenta para el usuario: {}", email);
                usuario.setIntentosFallidos(0);
                usuario.setCuentaBloqueada(false);
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            }
        });
    }

    public boolean usuarioExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Método para obtener el estado del usuario (útil para el controlador)
    public Optional<Usuario> obtenerEstadoUsuario(String email) {
        return usuarioRepository.findByEmail(email);
    }
}