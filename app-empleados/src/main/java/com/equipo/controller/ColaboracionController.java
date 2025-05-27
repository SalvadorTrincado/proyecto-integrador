package com.equipo.controller;

import com.equipo.dto.ColaboracionConChatDTO;
import com.equipo.dto.EnviarMensajeDTO;
import com.equipo.dto.InvitacionColaboracionDTO;
import com.equipo.entity.Usuario;
import com.equipo.service.ColaboracionService;
import com.equipo.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // Añadir @PathVariable si no está
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/empleado/colaboraciones")
public class ColaboracionController {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionController.class);

    private final ColaboracionService colaboracionService;
    private final UsuarioService usuarioService;

    @Autowired
    public ColaboracionController(ColaboracionService colaboracionService, UsuarioService usuarioService) {
        this.colaboracionService = colaboracionService;
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

    @PostMapping("/invitar")
    public String enviarInvitacion(@Valid @ModelAttribute("invitacionDTO") InvitacionColaboracionDTO invitacionDTO,
                                   BindingResult result, // Asegúrate que esté presente para la validación
                                   RedirectAttributes redirectAttributes) {
        UUID emisorId = obtenerUsuarioIdAutenticado();
        if (emisorId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Error de autenticación. No se pudo enviar la invitación.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        if (result.hasErrors()) {
            // Para que los errores de validación del DTO se muestren en la vista original,
            // es mejor que la vista 'area_personal' pueda manejar el DTO 'invitacionDTO'
            // y sus errores. Si no, un mensaje flash genérico es una alternativa.
            redirectAttributes.addFlashAttribute("invitacionDTO", invitacionDTO); // Devolver DTO con errores
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.invitacionDTO", result); // Devolver BindingResult
            redirectAttributes.addFlashAttribute("errorInvitacion", "El email proporcionado no es válido o está vacío."); // Mensaje genérico si lo prefieres
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        try {
            colaboracionService.invitar(emisorId, invitacionDTO.getEmailReceptor());
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Invitación enviada correctamente a " + invitacionDTO.getEmailReceptor() + ".");
        } catch (IllegalArgumentException | IllegalStateException | EntityNotFoundException e) {
            logger.warn("Error al enviar invitación de {} a {}: {}", emisorId, invitacionDTO.getEmailReceptor(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorInvitacion", e.getMessage()); // Mostrar error específico
        } catch (Exception e) {
            logger.error("Error inesperado al enviar invitación de {} a {}: {}", emisorId, invitacionDTO.getEmailReceptor(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorInvitacion", "Ocurrió un error inesperado al enviar la invitación.");
        }
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    @PostMapping("/aceptar/{idColaboracion}")
    public String aceptarInvitacion(@PathVariable String idColaboracion, RedirectAttributes redirectAttributes) {
        UUID receptorId = obtenerUsuarioIdAutenticado();
        if (receptorId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Error de autenticación.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }
        try {
            UUID colaboracionUUID = UUID.fromString(idColaboracion);
            colaboracionService.aceptarInvitacion(colaboracionUUID, receptorId);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Invitación aceptada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de colaboración inválido.");
        } catch (EntityNotFoundException | IllegalStateException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
        }
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    @PostMapping("/rechazar/{idColaboracion}")
    public String rechazarInvitacion(@PathVariable String idColaboracion, RedirectAttributes redirectAttributes) {
        UUID receptorId = obtenerUsuarioIdAutenticado();
        if (receptorId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Error de autenticación.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }
        try {
            UUID colaboracionUUID = UUID.fromString(idColaboracion);
            colaboracionService.rechazarInvitacion(colaboracionUUID, receptorId);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Invitación rechazada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de colaboración inválido.");
        } catch (EntityNotFoundException | IllegalStateException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
        }
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    @PostMapping("/cancelar/{idColaboracion}")
    public String cancelarColaboracion(@PathVariable String idColaboracion, RedirectAttributes redirectAttributes) {
        UUID usuarioActualId = obtenerUsuarioIdAutenticado();
        if (usuarioActualId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Error de autenticación. No se pudo cancelar la colaboración.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }
        try {
            UUID colaboracionUUID = UUID.fromString(idColaboracion);
            colaboracionService.cancelarColaboracion(colaboracionUUID, usuarioActualId);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "La colaboración ha sido cancelada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de colaboración inválido para cancelar.");
        } catch (EntityNotFoundException | IllegalStateException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
        }
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    @GetMapping("/{idColaboracion}/chat")
    public String mostrarChatColaboracion(@PathVariable String idColaboracion, Model model, RedirectAttributes redirectAttributes) {
        UUID usuarioActualId = obtenerUsuarioIdAutenticado();
        if (usuarioActualId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Debes iniciar sesión para ver el chat.");
            return "redirect:/autenticacion/paso1";
        }
        try {
            UUID colaboracionUUID = UUID.fromString(idColaboracion);
            ColaboracionConChatDTO chatDTO = colaboracionService.obtenerColaboracionConChatParaVista(colaboracionUUID, usuarioActualId);
            model.addAttribute("colaboracionChat", chatDTO);
            model.addAttribute("enviarMensajeDTO", new EnviarMensajeDTO()); // Para el formulario de nuevo mensaje
            return "aplicacion_corporativa/colaboracion/chat_colaboracion";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de colaboración inválido.");
            return "redirect:/aplicacion_corporativa/area_personal";
        } catch (EntityNotFoundException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
            return "redirect:/aplicacion_corporativa/area_personal";
        }
    }

    @PostMapping("/{idColaboracion}/chat/enviar")
    public String enviarMensajeChat(@PathVariable String idColaboracion,
                                    @Valid @ModelAttribute("enviarMensajeDTO") EnviarMensajeDTO enviarMensajeDTO,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model) { // Añadir Model para repoblar en caso de error
        UUID emisorId = obtenerUsuarioIdAutenticado();
        UUID colaboracionUUID;

        try {
            colaboracionUUID = UUID.fromString(idColaboracion);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de colaboración inválido.");
            return "redirect:/aplicacion_corporativa/area_personal"; // O a una página de error general
        }

        if (emisorId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Error de autenticación. No se pudo enviar el mensaje.");
            // Podrías redirigir a la página del chat con un error, pero sin poder cargar el chat,
            // es mejor redirigir al área personal o al login.
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        if (result.hasErrors()) {
            // Si hay errores de validación en el mensaje, necesitamos recargar la vista del chat
            // con los errores y los mensajes existentes.
            logger.warn("Errores de validación al enviar mensaje para colaboración {}: {}", idColaboracion, result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.enviarMensajeDTO", result);
            redirectAttributes.addFlashAttribute("enviarMensajeDTO", enviarMensajeDTO); // Para repoblar el campo
            redirectAttributes.addFlashAttribute("errorEnvioMensaje", "El mensaje no puede estar vacío o es demasiado largo.");
            return "redirect:/empleado/colaboraciones/" + idColaboracion + "/chat";
        }

        try {
            colaboracionService.enviarMensaje(colaboracionUUID, emisorId, enviarMensajeDTO.getTextoMensaje());
            // No es necesario mensaje flash de éxito aquí, la redirección recargará el chat con el nuevo mensaje.
        } catch (IllegalArgumentException | EntityNotFoundException | IllegalStateException | SecurityException e) {
            logger.error("Error al enviar mensaje en colaboración {}: {}", idColaboracion, e.getMessage());
            redirectAttributes.addFlashAttribute("errorEnvioMensaje", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al enviar mensaje en colaboración {}: {}", idColaboracion, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorEnvioMensaje", "Ocurrió un error inesperado al enviar el mensaje.");
        }
        return "redirect:/empleado/colaboraciones/" + idColaboracion + "/chat";
    }
}