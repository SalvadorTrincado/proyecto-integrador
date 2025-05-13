package com.equipo.controller;

import com.equipo.dto.LoginPaso1DTO;
import com.equipo.dto.LoginPaso2DTO;
import com.equipo.entity.Usuario;
import com.equipo.service.AutenticacionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AutenticacionController {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionController.class);

    private final AutenticacionService autenticacionService;

    @Autowired
    public AutenticacionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

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
    public String mostrarFormularioPaso2(Model model, HttpSession session) {
        String emailPaso1 = (String) session.getAttribute("emailPaso1");
        if (emailPaso1 == null) {
            return "redirect:/autenticacion/paso1";
        }
        model.addAttribute("loginPaso2DTO", new LoginPaso2DTO());
        model.addAttribute("emailPaso1", emailPaso1);
        return "aplicacion_corporativa/login_paso2";
    }

    @PostMapping("/autenticacion/paso2-post")
    public String procesarPaso2(
            @Valid @ModelAttribute("loginPaso2DTO") LoginPaso2DTO loginPaso2DTO,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("emailPaso1");
        if (email == null) {
            logger.warn("Email de paso 1 no encontrado en la sesión, redirigiendo.");
            return "redirect:/autenticacion/paso1-post";
        }

        if (result.hasErrors()) {
            logger.info("Errores de validación en el paso 2 para el email {}: {}", email, result.getAllErrors());
            model.addAttribute("emailPaso1", email);
            return "aplicacion_corporativa/login_paso2";
        }

        try {
            logger.info("Intentando autenticar al usuario con email: {}", email);
            // Spring Security maneja la autenticación.
            // No necesitamos llamar a autenticacionService.autenticarUsuario() ni loadUserByUsername().

        } catch (BadCredentialsException e) {
            logger.warn("Error de autenticación para el email {}: Contraseña incorrecta", email);
            Usuario usuario = autenticacionService.getUsuarioPorEmail(email);
            int intentosRestantes = autenticacionService.getMaxIntentosFallidos() - usuario.getIntentosFallidos();
            model.addAttribute("emailPaso1", email);
            model.addAttribute("error", "Contraseña incorrecta. Te quedan " + intentosRestantes + " intentos.");
            logger.info("Modelo después de BadCredentialsException: {}", model);
            return "aplicacion_corporativa/login_paso2"; // Retorno directo de la vista
        } catch (LockedException e) {
            logger.warn("Cuenta bloqueada para el email: {}", email);
            model.addAttribute("emailPaso1", email);
            model.addAttribute("error", "Cuenta bloqueada. Inténtalo de nuevo más tarde.");
            logger.info("Modelo después de LockedException: {}", model);
            return "aplicacion_corporativa/login_paso2"; // Retorno directo de la vista
        } catch (UsernameNotFoundException e) {
            logger.warn("Usuario no encontrado para el email: {}", email);
            model.addAttribute("emailPaso1", email);
            model.addAttribute("error", "Usuario no encontrado");
            logger.info("Modelo después de UsernameNotFoundException: {}", model);
            return "aplicacion_corporativa/login_paso2"; // Retorno directo de la vista
        }

        logger.info("Autenticación exitosa para el email: {}, redirigiendo al área personal.", email);
        // Si la autenticación es exitosa, Spring Security redirige.
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        String email = (String) session.getAttribute("emailPaso1");
        logger.info("Cerrando sesión para el email: {}", email);
        session.invalidate();
        return "redirect:/autenticacion/paso1";
    }
}