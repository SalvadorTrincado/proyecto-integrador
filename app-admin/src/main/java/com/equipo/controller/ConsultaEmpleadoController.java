package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin") // MVC endpoint path
public class ConsultaEmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/consulta-empleados")
    public String mostrarVistaConsultaEmpleados() {
        return "consulta/consulta_empleados"; // Path to the new Thymeleaf template
    }

    @GetMapping("/detalle/{id}")
    public String mostrarDetalleEmpleado(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID empleadoId = UUID.fromString(id);
            Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(empleadoId); //

            if (empleadoOpt.isPresent()) {
                model.addAttribute("empleado", empleadoOpt.get());
                return "consulta/detalle_empleado";
            } else {
                redirectAttributes.addFlashAttribute("error", "Empleado no encontrado con ID: " + id);
                return "redirect:/admin/consulta-empleados";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "ID de empleado inválido: " + id);
            return "redirect:/admin/consulta-empleados";
        }
    }
}
