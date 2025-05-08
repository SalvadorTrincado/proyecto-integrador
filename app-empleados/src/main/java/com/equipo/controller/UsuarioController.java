package com.equipo.controller;

import com.equipo.dto.RegistroUsuarioDTO;
import com.equipo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// Controlador que gestiona las vistas relacionadas con usuarios
@Controller
public class UsuarioController {

    // Inyecta el servicio de usuario para acceder a la lógica de negocio
    @Autowired
    private UsuarioService usuarioService;

    // Muestra el formulario de registro de usuario
    @GetMapping("/registrar_usuario")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioRegistroDTO", new RegistroUsuarioDTO());
        return "aplicacion_corporativa/registrar_usuario";
    }

    // Procesa el formulario de registro enviado por el usuario
    @PostMapping("/registrar_usuario_post")
    public String procesarRegistroUsuario(
            @Valid @ModelAttribute("usuarioRegistroDTO") RegistroUsuarioDTO registroUsuarioDTO,
            BindingResult result,
            Model model
    ) {
        // Si hay errores de validación, vuelve al formulario con mensaje de error
        if (result.hasErrors()) {
            model.addAttribute("error", "Hay errores en el formulario. Por favor, revisa los campos.");
            return "aplicacion_corporativa/registrar_usuario";
        }

        try {
            // Llama al servicio para registrar el usuario
            usuarioService.registrarUsuarioDesdeDTO(registroUsuarioDTO);
            model.addAttribute("mensaje", "Usuario " + registroUsuarioDTO.getEmail() + " registrado correctamente");
            return "redirect:/autenticacion/paso1";
        } catch (Exception e) {
            // Si ocurre una excepción, muestra el mensaje de error
            model.addAttribute("error", e.getMessage());
            return "aplicacion_corporativa/registrar_usuario";
        }
    }
}
