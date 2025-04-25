package com.equipo.controller;

import com.equipo.backend.dto.UsuarioDTO;
import com.equipo.backend.entity.Usuario;
import com.equipo.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro-usuario")
    public String registroUsuario(Model modelo) {
        modelo.addAttribute("usuario", new UsuarioDTO());
        return "";
    }

    @PostMapping("/registro-usuario")
    public String registroUsuario(@Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                                  Model modelo,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "";  // Retornar a la vista de registro con errores
        }

        try {
            usuarioService.registerNewUser(usuarioDTO);
        } catch (Exception e) {
            result.rejectValue("usuario", "error.usuario", e.getMessage());
            return "";  // Retornar a la vista de registro con errores
        }

        return "redirect:/";    // Redirigir a la página de inicio o a otra página de éxito
    }

}
