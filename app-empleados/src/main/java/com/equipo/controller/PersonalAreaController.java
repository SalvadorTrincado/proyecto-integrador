package com.equipo.controller;

import com.equipo.dto.ColaboracionDisplayDTO;
import com.equipo.dto.InvitacionColaboracionDTO;
import com.equipo.entity.Colaboracion;
import com.equipo.entity.Empleado;
import com.equipo.entity.Etiqueta;
import com.equipo.entity.Usuario;
import com.equipo.service.AutenticacionService;
import com.equipo.service.ColaboracionService;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PersonalAreaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalAreaController.class);
    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;
    private final AutenticacionService autenticacionService;
    private final ColaboracionService colaboracionService; // Nueva inyección

    @Autowired
    public PersonalAreaController(EmpleadoService empleadoService, UsuarioService usuarioService,
                                  AutenticacionService autenticacionService,
                                  ColaboracionService colaboracionService) { // Añadir al constructor
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
        this.autenticacionService = autenticacionService;
        this.colaboracionService = colaboracionService; // Asignar
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
            Usuario usuarioActualizadoConContadores = usuarioService.obtenerUsuarioPorEmail(userEmail)
                    .orElse(usuario);

            model.addAttribute("usuario", usuarioActualizadoConContadores);

            Integer contadorHttp = (Integer) session.getAttribute("contadorConexionesHttp");
            if (contadorHttp == null) {
                contadorHttp = 0;
            }
            contadorHttp++;
            session.setAttribute("contadorConexionesHttp", contadorHttp);
            model.addAttribute("contadorConexionesHttp", contadorHttp);

            session.setAttribute("usuarioAutenticadoId", usuarioActualizadoConContadores.getId());

            Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(usuarioActualizadoConContadores.getId());
            boolean esEmpleado = empleadoOpt.isPresent();
            model.addAttribute("esEmpleado", esEmpleado);

            if (!model.containsAttribute("invitacionDTO")) {
                model.addAttribute("invitacionDTO", new InvitacionColaboracionDTO());
            }

            if (esEmpleado) {
                Empleado empleado = empleadoOpt.get();
                String nombreCompleto = empleado.getNombre() + " " + empleado.getApellidos();
                model.addAttribute("nombreCompletoEmpleado", nombreCompleto);

                Set<String> nombresEtiquetas = empleado.getEtiquetas().stream()
                        .map(Etiqueta::getNombre)
                        .collect(Collectors.toSet());
                model.addAttribute("etiquetasDelEmpleado", nombresEtiquetas);
                logger.info("Usuario {} es un empleado: {}. Etiquetas: {}", userEmail, nombreCompleto, nombresEtiquetas);

                // Cargar colaboraciones usando los nuevos métodos del servicio que devuelven DTOs
                List<ColaboracionDisplayDTO> invitacionesEnviadas = colaboracionService.findInvitacionesEnviadasDisplay(empleado.getId());
                List<ColaboracionDisplayDTO> invitacionesRecibidas = colaboracionService.findInvitacionesRecibidasDisplay(empleado.getId());

                model.addAttribute("invitacionesEnviadas", invitacionesEnviadas);
                model.addAttribute("invitacionesRecibidas", invitacionesRecibidas);

            } else {
                model.addAttribute("mensajeOpcionRegistro", "Aún no has completado tu perfil de empleado.");
                model.addAttribute("etiquetasDelEmpleado", Collections.emptySet());
                model.addAttribute("invitacionesEnviadas", Collections.emptyList());
                model.addAttribute("invitacionesRecibidas", Collections.emptyList());
                logger.info("Usuario {} NO es un empleado. Mostrando opción para completar registro.", userEmail);
            }
        } else {
            logger.error("Usuario {} autenticado por Spring Security, pero no encontrado en la base de datos de Usuarios.", userEmail);
            model.addAttribute("errorGlobal", "Error crítico: Datos de usuario inconsistentes. Por favor, contacte a soporte.");
            SecurityContextHolder.clearContext();
            session.invalidate();
            return "redirect:/autenticacion/paso1?error=datos_usuario_no_encontrados";
        }

        return "aplicacion_corporativa/area_personal";
    }
}