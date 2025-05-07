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

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/resgistrar_usuario")
    public String registroUsuario(Model model) {
        model.addAttribute("usuarioRegistroDTO", new RegistroUsuarioDTO());
        return "aplicacion_corporativa/registrar_usuario";
    }

    @PostMapping("/resgistrar_usuario_post")
    public String registroUsuario_procesado(
            @Valid @ModelAttribute("usuarioRegistroDTO") RegistroUsuarioDTO registroUsuarioDTO,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {
            model.addAttribute("error", "Hay errores en el formulario. Por favor, revisa los campos.");
            return "aplicacion_corporativa/registrar_usuario";
        }

        try {
            // Llamamos al método del servicio para registrar el usuario
            usuarioService.registrarNuevoUsuarioDesdeRegistro(registroUsuarioDTO);
            model.addAttribute("mensaje", "Usuario " + registroUsuarioDTO.getEmail() + " registrado correctamente");
            return "redirect:/autenticacion/paso1"; // Redirigimos tras el registro exitoso
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // Mostramos el error si ocurre alguno
            return "aplicacion_corporativa/registrar_usuario"; // Volvemos al formulario con el error
        }
    }

}