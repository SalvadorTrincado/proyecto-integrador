package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // Para pasar datos a la vista si es necesario

@Controller
public class PasswordRecoveryController {

    @GetMapping("/recuperar-password")
    public String mostrarFormularioRecuperacion(Model model) {
        return "aplicacion_corporativa/recuperar_password"; // Nueva vista unificada
    }
}