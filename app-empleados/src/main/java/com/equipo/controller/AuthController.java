package com.equipo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.equipo.dto.LoginPaso1DTO;
import com.equipo.dto.LoginPaso2DTO;

@Controller
public class AuthController {

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
            return "aplicacion_corporativa/login_paso1";
        }
        String emailIntroducido = loginPaso1DTO.getEmail();
        session.setAttribute("emailPaso1", emailIntroducido);
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
            return "redirect:/autenticacion/paso1-post";
        }

        if (result.hasErrors()) {
            model.addAttribute("emailPaso1", email);
            return "aplicacion_corporativa/login_paso2";
        }

        String passwordIntroducida = loginPaso2DTO.getPassword();

        // Aquí iría la lógica para autenticar al usuario
        // (buscar en la base de datos y verificar la contraseña)

        // SIMULACIÓN DE AUTENTICACIÓN EXITOSA (¡REEMPLAZAR CON LA LÓGICA REAL!)
        if ("passwordValida".equals(passwordIntroducida) || 1 == 1) {
            // ¡Aquí deberías guardar la información del usuario en la sesión!
            return "redirect:/aplicacion_corporativa/area_personal";
        } else {
            model.addAttribute("emailPaso1", email);
            model.addAttribute("error", "La contraseña introducida no es correcta");
            return "aplicacion_corporativa/login_paso2";
        }
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/autenticacion/paso1";
    }
}