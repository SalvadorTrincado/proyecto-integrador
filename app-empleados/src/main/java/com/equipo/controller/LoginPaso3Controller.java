package com.equipo.controller;

import com.equipo.backend.entity.Empleado;
import com.equipo.backend.entity.Usuario;
import com.equipo.backend.service.UsuarioService;
import com.equipo.model.dto.LoginPaso3DTO;
import com.equipo.model.dto.LoginPaso1DTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class LoginPaso3Controller {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login/paso3")
    public String mostrarFormulario(Model model) {
        model.addAttribute("loginPaso3", new LoginPaso3DTO());
        model.addAttribute("mostrarErrores", false);
        return "login/paso3";
    }

    @PostMapping("/login/paso3")
    public String procesarFormulario(@Valid @ModelAttribute("loginPaso3") LoginPaso3DTO datos,
                                     BindingResult result,
                                     Model model,
                                     HttpSession session) {

        if (result.hasErrors()) {
            model.addAttribute("loginPaso3", datos);
            model.addAttribute("mostrarErrores", true);
            return "login/paso3";
        }

        // VALIDACIÓN REAL DE CONTRASEÑA
        LoginPaso1DTO paso1 = (LoginPaso1DTO) session.getAttribute("loginPaso1");
        String email = paso1.getEmail();
        String contrasena = datos.getContrasena();

        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email); //FALTA ALGO AQUI

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(contrasena, usuarioOpt.get().getClave())) {
            result.rejectValue("contrasena", "error.loginPaso3", "Credenciales incorrectas");
            model.addAttribute("mostrarErrores", true);
            return "login/paso3";
        }

        session.setAttribute("loginPaso3", datos);
        return "redirect:/login/resumen";
    }
}
