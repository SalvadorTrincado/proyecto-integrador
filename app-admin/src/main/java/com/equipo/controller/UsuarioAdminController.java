package com.equipo.controller;

import com.equipo.entity.Usuario;
import com.equipo.service.UsuarioAdminService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminController.class);
    private final UsuarioAdminService usuarioAdminService;

    @Autowired
    public UsuarioAdminController(UsuarioAdminService usuarioAdminService) {
        this.usuarioAdminService = usuarioAdminService;
    }

    @GetMapping("/gestion")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioAdminService.obtenerTodosLosUsuarios());
        return "admin/usuarios/gestion_usuarios";
    }

    @GetMapping("/detalle-usuario/{id}")
    public String verDetalleUsuario(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID usuarioId = UUID.fromString(id);
            Usuario usuario = usuarioAdminService.obtenerUsuarioPorId(usuarioId);
            model.addAttribute("usuario", usuario);
            return "admin/usuarios/detalle_usuario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de usuario inválido.");
            return "redirect:/admin/usuarios/gestion";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
            return "redirect:/admin/usuarios/gestion";
        }
    }


    @PostMapping("/bloquear/{id}")
    public String bloquearUsuario(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID usuarioId = UUID.fromString(id);
            usuarioAdminService.bloquearUsuario(usuarioId);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Usuario bloqueado correctamente.");
        } catch (IllegalArgumentException e) {
            logger.error("ID de usuario inválido para bloquear: {}", id, e);
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de usuario inválido.");
        } catch (EntityNotFoundException e) {
            logger.warn("No se encontró usuario para bloquear con ID: {}", id);
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
        } catch (IllegalStateException e) {
            logger.info("Intento de bloquear usuario que ya estaba bloqueado o en estado no modificable: {}", id);
            redirectAttributes.addFlashAttribute("mensajeInfoGlobal", e.getMessage());
        }
        return "redirect:/admin/usuarios/detalle-usuario/" + id;
    }

    @PostMapping("/desbloquear/{id}")
    public String desbloquearUsuario(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID usuarioId = UUID.fromString(id);
            usuarioAdminService.desbloquearUsuario(usuarioId);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Usuario desbloqueado correctamente.");
        } catch (IllegalArgumentException e) {
            logger.error("ID de usuario inválido para desbloquear: {}", id, e);
            redirectAttributes.addFlashAttribute("errorGlobal", "ID de usuario inválido.");
        } catch (EntityNotFoundException e) {
            logger.warn("No se encontró usuario para desbloquear con ID: {}", id);
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
        } catch (IllegalStateException e) {
            logger.info("Intento de desbloquear usuario que no estaba bloqueado o en estado no modificable: {}", id);
            redirectAttributes.addFlashAttribute("mensajeInfoGlobal", e.getMessage());
        }
        return "redirect:/admin/usuarios/detalle-usuario/" + id;
    }
}