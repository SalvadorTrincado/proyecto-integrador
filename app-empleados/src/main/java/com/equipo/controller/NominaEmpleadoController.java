package com.equipo.controller;

import com.equipo.dto.NominaDetalleDTO;
import com.equipo.service.NominaEmpleadoService;
import com.equipo.service.UsuarioService; // Para obtener el usuario actual
import com.equipo.entity.Usuario; // Para obtener el usuario actual

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.persistence.EntityNotFoundException;


import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/empleado/nominas") // Prefijo para las rutas de nóminas del empleado
public class NominaEmpleadoController {

    private final NominaEmpleadoService nominaEmpleadoService;
    private final UsuarioService usuarioService; // Para obtener el ID del usuario autenticado

    @Autowired
    public NominaEmpleadoController(NominaEmpleadoService nominaEmpleadoService, UsuarioService usuarioService) {
        this.nominaEmpleadoService = nominaEmpleadoService;
        this.usuarioService = usuarioService;
    }

    private UUID obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(username);
            return usuarioOpt.map(Usuario::getId).orElse(null);
        }
        return null;
    }

    @GetMapping("/consultar")
    public String consultarNominas(Model model,
                                   @PageableDefault(size = 10, sort = "fechaFinPeriodo") Pageable pageable,
                                   RedirectAttributes redirectAttributes) {
        UUID empleadoId = obtenerUsuarioIdAutenticado();
        if (empleadoId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "No se pudo identificar al usuario. Por favor, inicie sesión de nuevo.");
            return "redirect:/autenticacion/paso1";
        }

        try {
            Page<NominaDetalleDTO> nominas = nominaEmpleadoService.findNominasByEmpleado(empleadoId, pageable);
            model.addAttribute("nominas", nominas);
            model.addAttribute("pageableParams", (pageable.getSort().isSorted() ? "&sort=" + pageable.getSort().toString().replace(": ", ",") : ""));
            return "aplicacion_corporativa/empleado/lista_nominas_empleado"; // Nueva vista
        } catch (EntityNotFoundException e) {
            // Esto no debería ocurrir si el empleadoId viene de un usuario autenticado
            // que ya pasó por el registro de empleado.
            redirectAttributes.addFlashAttribute("errorGlobal", "Error al cargar sus datos de empleado.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }
    }

    @GetMapping("/detalle/{idNomina}")
    public String verDetalleNomina(@PathVariable String idNomina, Model model, RedirectAttributes redirectAttributes) {
        UUID empleadoId = obtenerUsuarioIdAutenticado();
        if (empleadoId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "No se pudo identificar al usuario. Por favor, inicie sesión de nuevo.");
            return "redirect:/autenticacion/paso1";
        }

        try {
            UUID nominaUUID = UUID.fromString(idNomina);
            Optional<NominaDetalleDTO> nominaOpt = nominaEmpleadoService.findNominaDetalleByIdAndEmpleadoId(nominaUUID, empleadoId);

            if (nominaOpt.isPresent()) {
                model.addAttribute("nomina", nominaOpt.get());
                return "aplicacion_corporativa/empleado/detalle_nomina_empleado"; // Nueva vista
            } else {
                redirectAttributes.addFlashAttribute("errorGlobal", "Nómina no encontrada o no tiene permiso para verla.");
                return "redirect:/empleado/nominas/consultar";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "El ID de la nómina proporcionado no es válido.");
            return "redirect:/empleado/nominas/consultar";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Nómina no encontrada.");
            return "redirect:/empleado/nominas/consultar";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
            return "redirect:/empleado/nominas/consultar";
        }
    }
}