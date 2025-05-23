package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.entity.Usuario;
import com.equipo.service.EmpleadoService;
import com.equipo.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.UUID;

@Controller
public class PersonalAreaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalAreaController.class);
    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;

    @Autowired
    public PersonalAreaController(EmpleadoService empleadoService, UsuarioService usuarioService) {
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/aplicacion_corporativa/area_personal")
    public String mostrarAreaPersonal(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                userEmail = ((UserDetails) principal).getUsername();
            } else {
                // En algunos casos, el principal podría ser solo el nombre de usuario como String
                userEmail = principal.toString();
            }
            logger.info("Usuario autenticado por Spring Security: {}", userEmail);
        } else {
            logger.warn("No hay usuario autenticado en SecurityContextHolder o es anonymousUser.");
            return "redirect:/autenticacion/paso1?error=no_autenticado";
        }

        if (userEmail == null) {
            logger.error("userEmail es null después de la verificación de autenticación.");
            return "redirect:/autenticacion/paso1?error=sesion_invalida";
        }

        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(userEmail);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            UUID usuarioId = usuario.getId();
            model.addAttribute("usuarioId", usuarioId);
            session.setAttribute("usuarioAutenticadoId", usuarioId); // Para el flujo de registro de empleado

            Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(usuarioId);
            boolean esEmpleado = empleadoOpt.isPresent();
            model.addAttribute("esEmpleado", esEmpleado);

            if (esEmpleado) {
                Empleado empleado = empleadoOpt.get();
                String nombreCompleto = empleado.getNombre() + " " + empleado.getApellidos();
                model.addAttribute("nombreCompletoEmpleado", nombreCompleto);
                logger.info("Usuario {} es un empleado: {}", userEmail, nombreCompleto);
            } else {
                model.addAttribute("mensajeOpcionRegistro", "Aún no has completado tu perfil de empleado.");
                logger.info("Usuario {} NO es un empleado. Mostrando opción para completar registro.", userEmail);
            }
        } else {
            logger.error("Usuario {} autenticado por Spring Security, pero no encontrado en la base de datos de Usuarios.", userEmail);
            model.addAttribute("errorGlobal", "Error crítico: Datos de usuario inconsistentes. Por favor, contacte a soporte.");
            SecurityContextHolder.clearContext(); // Limpiar contexto de seguridad
            session.invalidate(); // Invalidar sesión HTTP
            return "redirect:/autenticacion/paso1?error=datos_usuario_no_encontrados";
        }

        return "aplicacion_corporativa/area_personal";
    }
}