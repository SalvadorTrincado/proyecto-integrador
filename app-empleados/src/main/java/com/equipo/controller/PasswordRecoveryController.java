package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PasswordRecoveryController {

    @GetMapping("/recuperar_password")
    public String mostrarFormularioRecuperarPassword() {
        return "aplicacion_corporativa/recuperar_password";
    }
}