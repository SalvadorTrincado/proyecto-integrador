package com.equipo.service;

import com.equipo.dto.LoginPaso2DTO;
import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AutenticacionService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Constantes para la configuración del bloqueo
    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 5;

    public AutenticacionService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Excepciones personalizadas
    public static class CuentaBloqueadaException extends RuntimeException {
        public CuentaBloqueadaException(String message) {
            super(message);
        }
    }

    public static class ContraseñaIncorrectaException extends RuntimeException {
        public ContraseñaIncorrectaException(String message) {
            super(message);
        }
    }

    /**
     * Autentica a un usuario y maneja el bloqueo de la cuenta en caso de intentos fallidos.
     * Este método se llama desde otras partes de la aplicación, NO directamente por Spring Security durante el inicio de sesión.
     *
     * @param loginPaso2DTO El DTO que contiene la contraseña proporcionada por el usuario.
     * @param email          El email del usuario.
     * @return true si la autenticación es exitosa, false si no.
     * @throws CuentaBloqueadaException Si la cuenta está bloqueada.
     * @throws ContraseñaIncorrectaException Si la contraseña es incorrecta.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     */
    @Transactional
    public boolean autenticarUsuario(LoginPaso2DTO loginPaso2DTO, String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Primero, verificamos si la cuenta está bloqueada
        if (usuario.isCuentaBloqueada()) {
            // Si la cuenta está bloqueada, verificamos si ha pasado el tiempo de bloqueo
            if (usuario.getTiempoDesbloqueo().isBefore(LocalDateTime.now())) {
                // Si el tiempo de desbloqueo ha expirado, desbloqueamos la cuenta
                desbloquearCuenta(usuario);
            } else {
                // Si la cuenta aún está bloqueada, lanzamos una excepción
                throw new CuentaBloqueadaException("Cuenta bloqueada. Inténtalo de nuevo más tarde.");
            }
        }

        // Verificamos si la contraseña proporcionada coincide con la contraseña cifrada en la base de datos
        if (passwordEncoder.matches(loginPaso2DTO.getPassword(), usuario.getPassword())) {
            // Si la autenticación es exitosa, restablecemos el contador de intentos fallidos
            resetearIntentosFallidos(usuario);
            return true;
        } else {
            // Si la autenticación falla, incrementamos el contador de intentos fallidos
            incrementarIntentosFallidos(usuario);
            throw new ContraseñaIncorrectaException("Contraseña incorrecta");
        }
    }

    /**
     * Carga los detalles del usuario para la autenticación de Spring Security.
     * Este método es llamado por Spring Security.
     *
     * @param email El email del usuario.
     * @return UserDetails Los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     * @throws CuentaBloqueadaException Si la cuenta está bloqueada.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        if (usuario.isCuentaBloqueada()) {
            throw new CuentaBloqueadaException("Cuenta Bloqueada");
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword()) // La contraseña ya debe estar encriptada con BCrypt
                .roles("USER")
                .build();
    }

    // Métodos auxiliares para manejar el bloqueo y desbloqueo
    private void incrementarIntentosFallidos(Usuario usuario) {
        usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
        usuario.setUltimoIntentoFallido(LocalDateTime.now());
        if (usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
            bloquearCuenta(usuario);
        }
        usuarioRepository.save(usuario);
    }

    private void resetearIntentosFallidos(Usuario usuario) {
        usuario.setIntentosFallidos(0);
        usuario.setUltimoIntentoFallido(null);
        usuario.setCuentaBloqueada(false);
        usuario.setTiempoDesbloqueo(null);
        usuarioRepository.save(usuario);
    }

    private void bloquearCuenta(Usuario usuario) {
        usuario.setCuentaBloqueada(true);
        usuario.setTiempoDesbloqueo(LocalDateTime.now().plusMinutes(TIEMPO_BLOQUEO_MINUTOS));
        usuarioRepository.save(usuario);
    }

    private void desbloquearCuenta(Usuario usuario) {
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
        usuario.setTiempoDesbloqueo(null);
        usuarioRepository.save(usuario);
    }

    public int getMaxIntentosFallidos()
    {
        return MAX_INTENTOS_FALLIDOS;
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }
}