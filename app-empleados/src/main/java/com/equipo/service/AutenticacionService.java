package com.equipo.service;

import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    public static final int MAX_INTENTOS_FALLIDOS = 3; // Límite de intentos fallidos
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
                    throw new BadCredentialsException("Usuario no encontrado o credenciales inválidas"); //
                });

        if (usuario.isCuentaBloqueada()) {
            if (usuario.getFechaBloqueo() != null &&
                    LocalDateTime.now().isBefore(usuario.getFechaBloqueo().plusMinutes(DURACION_BLOQUEO_MINUTOS))) {
                long minutosRestantes = ChronoUnit.MINUTES.between(LocalDateTime.now(), usuario.getFechaBloqueo().plusMinutes(DURACION_BLOQUEO_MINUTOS)) + 1;
                logger.warn("Intento de acceso a cuenta bloqueada para el usuario: {}. Bloqueo activo por {} minutos más.", email, minutosRestantes);
                throw new LockedException("La cuenta de usuario está bloqueada. Inténtalo de nuevo en aproximadamente " + minutosRestantes + " minuto(s)."); //
            } else {
                // El tiempo de bloqueo ha expirado, desbloquear la cuenta
                logger.info("Tiempo de bloqueo expirado para el usuario: {}. Desbloqueando cuenta.", email);
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0); // Resetear intentos también
                usuario.setFechaBloqueo(null);
                // No es necesario guardar aquí explícitamente si la transacción de login exitoso lo hace después,
                // o si el flujo de login fallido (pero expirado) no llega a este punto para guardar.
                // Sin embargo, si loadUserByUsername es el único punto de desbloqueo automático, se debería guardar.
                // Para mayor claridad y asegurar el desbloqueo, guardamos aquí si se modifica.
                usuarioRepository.save(usuario); // Guardar el estado desbloqueado
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
                if (usuario.getFechaBloqueo() != null &&
                        LocalDateTime.now().isBefore(usuario.getFechaBloqueo().plusMinutes(DURACION_BLOQUEO_MINUTOS))) {
                    logger.warn("Intento fallido en cuenta activamente bloqueada para el usuario: {}", email);
                    return;
                } else {
                    logger.info("Bloqueo expirado para {}, registrando nuevo intento fallido.", email);
                    usuario.setCuentaBloqueada(false);
                    usuario.setIntentosFallidos(0);
                    usuario.setFechaBloqueo(null);
                }
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
            boolean cambiosRealizados = false;
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                cambiosRealizados = true;
            }
            if (usuario.isCuentaBloqueada()) {
                // Esto cubre el caso donde el bloqueo expiró, se intentó login, fue exitoso,
                // y ahora formalmente reseteamos el estado de bloqueo.
                usuario.setCuentaBloqueada(false);
                usuario.setFechaBloqueo(null);
                cambiosRealizados = true;
            }

            // Tarea 6d: Incrementar contador de conexiones válidas para Usuario
            Integer contadorActual = usuario.getContadorConexionesValidas();
            usuario.setContadorConexionesValidas((contadorActual == null ? 0 : contadorActual) + 1);
            cambiosRealizados = true; // Siempre hay un cambio aquí por el incremento del contador

            if (cambiosRealizados) {
                logger.info("Intento exitoso para {}. Reseteando fallos/bloqueo si aplicable. Conexiones válidas: {}", email, usuario.getContadorConexionesValidas());
                usuarioRepository.save(usuario);
            } else {
                // Solo loguear si no hubo otros cambios, aunque esto es menos probable ahora con el contador siempre actualizándose.
                logger.info("Intento exitoso para {}. Conexiones válidas: {}", email, usuario.getContadorConexionesValidas());
            }
        });
    }

    public boolean usuarioExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Optional<Usuario> obtenerEstadoUsuario(String email) {
        return usuarioRepository.findByEmail(email);
    }
}