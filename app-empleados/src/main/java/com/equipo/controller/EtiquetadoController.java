//FALTA TRABAJO ANDRIY

package com.equipo.controller;

import com.equipo.model.dto.EtiquetadoDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EtiquetadoController {

    @GetMapping("/etiquetado")
    public String mostrarFormulario(Model model) {
        model.addAttribute("etiquetadoDTO", new EtiquetadoDTO());

        // Simulación de empleados (ID y nombre)
        model.addAttribute("empleados", List.of(
                new EmpleadoMock(1L, "Ana Pérez"),
                new EmpleadoMock(2L, "Carlos Ruiz")
        ));

        // Simulación de etiquetas disponibles
        model.addAttribute("etiquetas", List.of("Backend", "RRHH", "Diseño"));

        return "empleados/etiquetado";
    }

    @PostMapping("/etiquetado")
    public String procesarFormulario(@ModelAttribute("etiquetadoDTO") @Valid EtiquetadoDTO dto,
                                     Model model) {

        System.out.println("Empleado ID: " + dto.getEmpleadoId());
        System.out.println("Etiquetas seleccionadas: " + dto.getEtiquetasSeleccionadas());

        model.addAttribute("mensaje", "Etiquetas aplicadas correctamente (simulado)");

        return "redirect:/empleados/etiquetado";
    }

    // Clase simulada solo para el formulario
    public record EmpleadoMock(Long id, String nombre) {}
}
