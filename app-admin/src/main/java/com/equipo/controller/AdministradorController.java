package com.equipo.controller;

import com.equipo.dto.AdministradorDTO;
import com.equipo.entity.Administrador;
import com.equipo.service.AdministradorServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller // Indica que esta clase es un controlador de Spring MVC
public class AdministradorController {

    private final AdministradorServicio administradorServicio;

    @Autowired
    public AdministradorController(AdministradorServicio administradorServicio) {
        this.administradorServicio = administradorServicio;
    }

    @GetMapping("/login/administrador") // Muestra la página de login para administradores
    public String mostrarFormularioLogin(HttpSession sesion) {
        if (sesion.getAttribute("adminId") != null) {
            return "redirect:/admin/area_personal"; // Si ya hay sesión, redirige al área personal
        }
        return "login_administrador";
    }

    @PostMapping("/login/administrador-post") // Procesa el intento de inicio de sesión de un administrador
    public String procesarLogin(@RequestParam("email") String correoElectronico,
                                @RequestParam("password") String contraseña,
                                Model modelo,
                                HttpSession sesion) {
        Optional<Administrador> administradorOptional = administradorServicio.autenticarAdministrador(correoElectronico, contraseña);

        if (administradorOptional.isPresent()) {
            Administrador administrador = administradorOptional.get();
            sesion.setAttribute("adminId", administrador.getId()); // Guarda el ID del administrador en la sesión
            return "redirect:/admin/area_personal"; // Redirige al área personal
        } else {
            modelo.addAttribute("error", "Credenciales inválidas. Por favor, inténtalo de nuevo."); // Asegúrate de tener esta línea
            // Incrementa el contador de intentos fallidos de autenticación
            administradorServicio.incrementarNumeroAutenticacionesFallidas(correoElectronico);
            return "login_administrador"; // Muestra de nuevo la página de login con el error
        }
    }

    @GetMapping("/admin/area_personal") // Muestra el área personal del administrador
    public String mostrarDashboard(HttpSession sesion, Model modelo) {
        Object adminId = sesion.getAttribute("adminId");
        if (adminId != null) {
            return "area_personal"; // Muestra el área personal
        } else {
            return "redirect:/login/administrador"; // Redirige al login si no hay sesión
        }
    }

    @PostMapping("/logout") // Procesa el cierre de sesión del administrador
    public String cerrarSesion(HttpSession sesion) {
        sesion.invalidate(); // Invalida la sesión actual
        return "redirect:/login/administrador"; // Redirige a la página de login
    }

}