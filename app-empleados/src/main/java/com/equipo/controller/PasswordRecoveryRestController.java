package com.equipo.controller;

import com.equipo.dto.RecuperacionClaveDTO;
import com.equipo.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/password-recovery")
public class PasswordRecoveryRestController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryRestController.class);

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email no puede estar vacío."));
        }
        if (usuarioService.obtenerUsuarioPorEmail(email).isPresent()) {
            logger.info("Verificación de email exitosa para: {}", email);
            return ResponseEntity.ok(Map.of("mensaje", "Email verificado."));
        } else {
            logger.warn("Verificación de email fallida, email no encontrado: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "El email no se encuentra registrado."));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody RecuperacionClaveDTO recuperacionClaveDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            // Si hay un error global de coincidencia de contraseñas, también lo añadimos.
            bindingResult.getGlobalErrors().forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));
            logger.warn("Errores de validación al resetear contraseña para {}: {}", recuperacionClaveDTO.getEmail(), errors);
            return ResponseEntity.badRequest().body(errors);
        }

        boolean actualizado = usuarioService.actualizarPasswordPorEmail(
                recuperacionClaveDTO.getEmail(),
                recuperacionClaveDTO.getNuevaPassword()
        );

        if (actualizado) {
            logger.info("Contraseña reseteada exitosamente para: {}", recuperacionClaveDTO.getEmail());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
        } else {
            // Esto no debería ocurrir si check-email fue llamado antes y tuvo éxito.
            logger.error("Error al resetear contraseña: Usuario no encontrado con email {} (debería haber sido verificado antes).", recuperacionClaveDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado."));
        }
    }
}