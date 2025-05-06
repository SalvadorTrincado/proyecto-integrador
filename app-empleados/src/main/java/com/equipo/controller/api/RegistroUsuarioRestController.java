package com.equipo.controller.api;

import com.equipo.backend.dto.RegistroUsuarioRequestDTO;
import com.equipo.backend.dto.RegistroUsuarioResponseDTO;
import com.equipo.backend.entity.Usuario;
import com.equipo.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth") // Prefijo más apropiado para la autenticación
public class RegistroUsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro") // Endpoint específico para el registro de usuario
    public ResponseEntity<RegistroUsuarioResponseDTO> registrarNuevoUsuario(
            @Valid @RequestBody RegistroUsuarioRequestDTO registroRequest,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            StringBuilder errores = new StringBuilder();
            result.getFieldErrors().forEach(error -> errores.append(error.getDefaultMessage()).append(". "));
            RegistroUsuarioResponseDTO response = new RegistroUsuarioResponseDTO(false, errores.toString(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request para errores de validación
        }

        if (!registroRequest.isClavesCoincidentes()) {
            RegistroUsuarioResponseDTO response = new RegistroUsuarioResponseDTO(false, "Las contraseñas no coinciden.", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        try {
            if (usuarioService.existsByEmail(registroRequest.getEmail())) {
                RegistroUsuarioResponseDTO response = new RegistroUsuarioResponseDTO(false, "El email ya está registrado.", null);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict si el email ya existe
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(registroRequest.getEmail()); // Usamos el email como nombre de usuario
            nuevoUsuario.setEmail(registroRequest.getEmail());
            nuevoUsuario.setClave(usuarioService.codificarContrasena(registroRequest.getClave())); // Usamos el servicio para codificar
            nuevoUsuario.setFechaCreacion(LocalDateTime.now());

            usuarioService.guardar(nuevoUsuario); // Asumo que tienes un método guardar en tu servicio

            RegistroUsuarioResponseDTO response = new RegistroUsuarioResponseDTO(true, "Usuario registrado correctamente.", registroRequest.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created para registro exitoso

        } catch (Exception e) {
            RegistroUsuarioResponseDTO response = new RegistroUsuarioResponseDTO(false, "Error al registrar el usuario: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}