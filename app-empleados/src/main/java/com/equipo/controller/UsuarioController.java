package com.equipo.controller;

import com.equipo.dto.RegistroUsuarioDTO;
import com.equipo.entity.Usuario; // Asegúrate de importar Usuario
import com.equipo.service.UsuarioService;
import jakarta.servlet.http.HttpSession; // Importar HttpSession
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar UserDetailsService
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importar RedirectAttributes

@Controller
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService; // Tu AutenticacionService implementa esta interfaz

    @GetMapping("/registrar_usuario")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioRegistroDTO", new RegistroUsuarioDTO());
        return "aplicacion_corporativa/registrar_usuario";
    }

    @PostMapping("/registrar_usuario_post")
    public String procesarRegistroUsuario(
            @Valid @ModelAttribute("usuarioRegistroDTO") RegistroUsuarioDTO registroUsuarioDTO,
            BindingResult result,
            Model model,
            HttpSession session, // Añadir HttpSession
            RedirectAttributes redirectAttributes // Añadir RedirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Hay errores en el formulario. Por favor, revisa los campos.");
            return "aplicacion_corporativa/registrar_usuario";
        }

        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuarioDesdeDTO(registroUsuarioDTO);
            logger.info("Usuario {} registrado con éxito a través de DTO.", nuevoUsuario.getEmail());

            // Autenticar programáticamente al nuevo usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(nuevoUsuario.getEmail());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            session.setAttribute("usuarioAutenticadoId", nuevoUsuario.getId());
            logger.info("ID de usuario {} guardado en sesión como 'usuarioAutenticadoId'.", nuevoUsuario.getId());

            // Incrementar contador de conexiones válidas (simulando un primer login)
            // El AutenticacionService.registrarIntentoExitoso lo haría en un flujo normal de login.
            // Aquí, si es necesario un conteo inmediato, se podría llamar a un método similar o
            // confiar en que la primera acción autenticada lo registre.
            // Por simplicidad, asumimos que el PersonalAreaController o el login posterior lo manejarán.

            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Cuenta de usuario creada. Por favor, complete su perfil de empleado.");
            return "redirect:/registro_empleado_paso1"; // Redirigir al primer paso del registro de empleado

        } catch (Exception e) {
            logger.error("Error al registrar o autenticar programáticamente al usuario: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "aplicacion_corporativa/registrar_usuario";
        }
    }
}