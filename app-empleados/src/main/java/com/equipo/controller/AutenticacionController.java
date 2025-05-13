package com.equipo.controller;

import com.equipo.dto.LoginPaso1DTO;
import com.equipo.dto.LoginPaso2DTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AutenticacionController {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionController.class);

    @GetMapping("/autenticacion/paso1")
    public String mostrarFormularioPaso1(Model model) {
        model.addAttribute("loginPaso1DTO", new LoginPaso1DTO());
        return "aplicacion_corporativa/login_paso1";
    }

    @PostMapping("/autenticacion/paso1-post")
    public String procesarPaso1(
            @Valid @ModelAttribute("loginPaso1DTO") LoginPaso1DTO loginPaso1DTO,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            logger.info("Errores de validación en el paso 1: {}", result.getAllErrors());
            return "aplicacion_corporativa/login_paso1";
        }
        session.setAttribute("emailPaso1", loginPaso1DTO.getEmail());
        return "redirect:/autenticacion/paso2";
    }

    @GetMapping("/autenticacion/paso2")
    public String mostrarFormularioPaso2(Model model, HttpSession session, @RequestParam(value = "error", required = false) String error) {
        String emailPaso1 = (String) session.getAttribute("emailPaso1");
        if (emailPaso1 == null) {
            return "redirect:/autenticacion/paso1";
        }
        model.addAttribute("loginPaso2DTO", new LoginPaso2DTO());
        model.addAttribute("emailPaso1", emailPaso1);
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        return "aplicacion_corporativa/login_paso2";
    }

    @PostMapping("/autenticacion/paso2-post")
    public String procesarPaso2(
            @Valid @ModelAttribute("loginPaso2DTO") LoginPaso2DTO loginPaso2DTO,
            BindingResult result,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("emailPaso1");
        if (email == null) {
            logger.warn("Email de paso 1 no encontrado en la sesión, redirigiendo.");
            return "redirect:/autenticacion/paso1";
        }

        if (result.hasErrors()) {
            logger.info("Errores de validación en el paso 2 para el email {}: {}", email, result.getAllErrors());
            return "aplicacion_corporativa/login_paso2";
        }
        
        return "redirect:/autenticacion/paso2-post"; // Spring Security intercepta esta petición
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        String email = (String) session.getAttribute("emailPaso1");
        logger.info("Cerrando sesión para el email: {}", email);
        session.invalidate();
        return "redirect:/autenticacion/paso1";
    }
}