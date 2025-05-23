package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.entity.Etiqueta; // Importar Etiqueta
import com.equipo.entity.Usuario;
import com.equipo.service.EmpleadoService;
import com.equipo.service.UsuarioService;
// Asumiremos que existe un EtiquetaService accesible o que parte de su lógica está en EmpleadoService
// Si EtiquetaService está en app-admin y no es accesible directamente, necesitaremos una forma de obtener las etiquetas.
// Por ahora, asumiré que Empleado entidad ya tiene la colección de etiquetas cargada (LAZY o EAGER).
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

import java.util.Collections; // Para set vacío
import java.util.Optional;
import java.util.Set; // Importar Set
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PersonalAreaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalAreaController.class);
    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;
    // No inyectamos EtiquetaService directamente aquí para mantener app-empleados más desacoplado de la gestión activa de etiquetas.
    // Las etiquetas se obtendrán a través de la entidad Empleado.

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
            session.setAttribute("usuarioAutenticadoId", usuarioId);

            Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(usuarioId);
            boolean esEmpleado = empleadoOpt.isPresent();
            model.addAttribute("esEmpleado", esEmpleado);

            if (esEmpleado) {
                Empleado empleado = empleadoOpt.get();
                String nombreCompleto = empleado.getNombre() + " " + empleado.getApellidos();
                model.addAttribute("nombreCompletoEmpleado", nombreCompleto);

                // Obtener y pasar las etiquetas del empleado a la vista
                // Asegurarse de que la colección de etiquetas se inicialice si es LAZY y se accede aquí.
                // Hibernate manejará esto si la sesión está activa.
                Set<String> nombresEtiquetas = empleado.getEtiquetas().stream()
                        .map(Etiqueta::getNombre)
                        .collect(Collectors.toSet());
                model.addAttribute("etiquetasDelEmpleado", nombresEtiquetas);
                logger.info("Usuario {} es un empleado: {}. Etiquetas: {}", userEmail, nombreCompleto, nombresEtiquetas);

            } else {
                model.addAttribute("mensajeOpcionRegistro", "Aún no has completado tu perfil de empleado.");
                model.addAttribute("etiquetasDelEmpleado", Collections.emptySet()); // Para que el atributo exista en la vista
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