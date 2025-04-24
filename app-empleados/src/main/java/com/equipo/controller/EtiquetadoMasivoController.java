package com.equipo.controller;

import com.equipo.model.dto.EtiquetadoMasivoDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EtiquetadoMasivoController {

    @GetMapping("/etiquetado-masivo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("etiquetadoMasivoDTO", new EtiquetadoMasivoDTO());

        // Simulados: empleados y etiquetas
        model.addAttribute("empleados", List.of(
                new EmpleadoMock(1L, "Ana Pérez"),
                new EmpleadoMock(2L, "Carlos Ruiz"),
                new EmpleadoMock(3L, "Lucía Torres")
        ));

        model.addAttribute("etiquetas", List.of("Backend", "Diseño", "RRHH"));

        return "empleados/etiquetado-masivo";
    }

    @PostMapping("/etiquetado-masivo")
    public String procesarFormulario(@ModelAttribute @Valid EtiquetadoMasivoDTO dto,
                                     Model model) {

        System.out.println("Empleados seleccionados: " + dto.getEmpleadosSeleccionados());
        System.out.println("Etiquetas aplicadas: " + dto.getEtiquetasSeleccionadas());

        model.addAttribute("mensaje", "Etiquetas aplicadas correctamente (simulado)");
        return "redirect:/empleados/etiquetado-masivo";
    }

    public record EmpleadoMock(Long id, String nombre) {}
}
