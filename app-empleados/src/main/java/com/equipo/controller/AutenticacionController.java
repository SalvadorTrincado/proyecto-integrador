package com.equipo.controller;

import com.equipo.dto.LoginPaso1DTO;
import com.equipo.dto.LoginPaso2DTO;
import com.equipo.entity.Usuario; // Importar Usuario
import com.equipo.service.AutenticacionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional; // Importar Optional

@Controller
public class AutenticacionController {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionController.class);

    @Autowired
    private AutenticacionService autenticacionService;

    @GetMapping("/autenticacion/paso1")
    public String mostrarFormularioPaso1(Model model, @RequestParam(value = "error", required = false) String error,  @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            // Los errores específicos se manejarán en el paso 2
            model.addAttribute("errorGlobalPaso1", "Error en el proceso de login.");
        }
        if (logout != null) {
            model.addAttribute("mensajeLogout", "Has cerrado sesión correctamente.");
        }
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
            return "aplicacion_corporativa/login_paso1";
        }

        String emailIntroducido = loginPaso1DTO.getEmail();
        Optional<Usuario> usuarioOpt = autenticacionService.obtenerEstadoUsuario(emailIntroducido);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "No existe ningún usuario con ese correo.");
            return "aplicacion_corporativa/login_paso1";
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.isCuentaBloqueada()) {
            if (usuario.getFechaBloqueo() != null &&
                    LocalDateTime.now().isBefore(usuario.getFechaBloqueo().plusMinutes(AutenticacionService.DURACION_BLOQUEO_MINUTOS))) {
                long minutosRestantes = ChronoUnit.MINUTES.between(LocalDateTime.now(), usuario.getFechaBloqueo().plusMinutes(AutenticacionService.DURACION_BLOQUEO_MINUTOS)) + 1;
                model.addAttribute("error", "Tu cuenta está bloqueada. Inténtalo de nuevo en aproximadamente " + minutosRestantes + " minuto(s).");
                return "aplicacion_corporativa/login_paso1"; // Mostrar error en paso 1
            } else {
                // Si el bloqueo ha expirado aquí, el loadUserByUsername en AutenticacionService lo manejará
                // y desbloqueará si se intenta un login correcto.
                logger.info("El bloqueo de la cuenta para {} ha expirado, procediendo al paso 2.", emailIntroducido);
            }
        }

        session.setAttribute("emailPaso1", emailIntroducido);
        return "redirect:/autenticacion/paso2";
    }


    @GetMapping("/autenticacion/paso2")
    public String mostrarFormularioPaso2(Model model, HttpSession session, @RequestParam(value = "error", required = false) String errorKey) {
        String emailPaso1 = (String) session.getAttribute("emailPaso1");
        if (emailPaso1 == null) {
            return "redirect:/autenticacion/paso1";
        }
        model.addAttribute("loginPaso2DTO", new LoginPaso2DTO());
        model.addAttribute("emailPaso1", emailPaso1);

        if (errorKey != null) {
            Optional<Usuario> usuarioOpt = autenticacionService.obtenerEstadoUsuario(emailPaso1);
            if ("bloqueado".equals(errorKey)) {
                if (usuarioOpt.isPresent() && usuarioOpt.get().getFechaBloqueo() != null) {
                    long minutosRestantes = ChronoUnit.MINUTES.between(LocalDateTime.now(), usuarioOpt.get().getFechaBloqueo().plusMinutes(AutenticacionService.DURACION_BLOQUEO_MINUTOS)) +1;
                    if (minutosRestantes > 0) {
                        model.addAttribute("error", "Tu cuenta está bloqueada. Inténtalo de nuevo en aproximadamente " + minutosRestantes + " minuto(s).");
                    } else {
                        model.addAttribute("error", "Tu cuenta estaba bloqueada, pero ya puedes intentarlo. Introduce tu contraseña.");
                    }
                } else {
                    model.addAttribute("error", "Tu cuenta está bloqueada debido a múltiples intentos fallidos.");
                }
            } else if ("credenciales".equals(errorKey)) {
                String mensajeError = "Email o contraseña incorrectos.";
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    int intentosRestantes = AutenticacionService.MAX_INTENTOS_FALLIDOS - usuario.getIntentosFallidos();
                    if (intentosRestantes > 0 && !usuario.isCuentaBloqueada()) {
                        mensajeError += " Te quedan " + intentosRestantes + " intento(s).";
                    }
                }
                model.addAttribute("error", mensajeError);
            } else {
                model.addAttribute("error", "Fallo en la autenticación. Inténtalo de nuevo.");
            }
        }
        return "aplicacion_corporativa/login_paso2";
    }
}