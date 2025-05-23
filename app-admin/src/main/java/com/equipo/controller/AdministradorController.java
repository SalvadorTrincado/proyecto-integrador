package com.equipo.controller;

import com.equipo.dto.AdministradorDTO;
import com.equipo.entity.Administrador;
import com.equipo.service.AdministradorServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller // Indica que esta clase es un controlador de Spring MVC
public class AdministradorController {

    private final AdministradorServicio administradorServicio;

    @Autowired
    public AdministradorController(AdministradorServicio administradorServicio) {
        this.administradorServicio = administradorServicio;
    }

    @GetMapping("/login/administrador") // Muestra la página de login para administradores
    public String mostrarFormularioLogin(Model modelo) {
        modelo.addAttribute("administradorDTO", new AdministradorDTO());

        administradorServicio.cargarAdministradoresDesdeJson();
        return "login_administrador";
    }

    @PostMapping("/login/administrador-post") // Procesa el intento de inicio de sesión de un administrador
    public String procesarLogin(@ModelAttribute("administradorDTO") AdministradorDTO administradorDTO,
                                HttpSession sesion,
                                Model modelo,
                                BindingResult result) {
        if(result.hasErrors()) {
            return "login_administrador";
        }

        Optional<Administrador> administradorOptional = administradorServicio
                .autenticarAdministrador(administradorDTO.getEmail(), administradorDTO.getClave());

        if (administradorOptional.isPresent()) {
            Administrador administrador = administradorOptional.get();
            sesion.setAttribute("adminId", administrador.getId());
            return "redirect:/admin/area_personal";
        } else {
            modelo.addAttribute("error", "Credenciales inválidas. Por favor, inténtalo de nuevo.");
            administradorServicio.incrementarNumeroAutenticacionesFallidas(administradorDTO.getEmail());
            return "login_administrador";
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

    @GetMapping("/admin/buscar-empleados")
    public String mostrarBusquedaEmpleados(HttpSession sesion, Model modelo) {
        Object adminId = sesion.getAttribute("adminId");
        if (adminId != null) {
            // Opcional: cargar todos los empleados al inicio o dejar que AJAX los cargue
            // modelo.addAttribute("empleados", empleadoServicio.obtenerTodosLosEmpleados());
            return "buscar_empleados"; // Una nueva vista dedicada a la búsqueda
        } else {
            return "redirect:/login/administrador";
        }
    }

}