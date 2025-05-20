package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PasswordRecoveryController {

    @GetMapping("/recuperar_password")
    public String mostrarFormularioRecuperarPassword() {
        return "aplicacion_corporativa/recuperar_password";
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    public String enviarCorreoRecuperacion(@RequestParam("email") String email) {
        // Aquí puedes validar el correo y simular el envío
        // O usar tu servicio real
        System.out.println("Enviando correo a: " + email);
        return "Se ha enviado un enlace de recuperación a tu correo.";
    }
}