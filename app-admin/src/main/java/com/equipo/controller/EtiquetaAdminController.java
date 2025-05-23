package com.equipo.controller;

import com.equipo.entity.Etiqueta;
import com.equipo.entity.Empleado;
import com.equipo.service.EtiquetaService;
import com.equipo.service.EmpleadoService; // Para listar empleados
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/etiquetas")
public class EtiquetaAdminController {

    private final EtiquetaService etiquetaService;
    private final EmpleadoService empleadoService;

    @Autowired
    public EtiquetaAdminController(EtiquetaService etiquetaService, EmpleadoService empleadoService) {
        this.etiquetaService = etiquetaService;
        this.empleadoService = empleadoService;
    }

    // --- Gestión de Etiquetas (CRUD) ---

    @GetMapping
    public String listarEtiquetas(Model model) {
        model.addAttribute("etiquetas", etiquetaService.obtenerTodasLasEtiquetas());
        model.addAttribute("etiqueta", new Etiqueta()); // Para el formulario de nueva etiqueta
        return "admin/etiquetas/gestion_etiquetas";
    }

    @PostMapping("/crear")
    public String crearEtiqueta(@Valid @ModelAttribute("etiqueta") Etiqueta etiqueta,
                                BindingResult result,
                                RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("etiquetas", etiquetaService.obtenerTodasLasEtiquetas());
            return "admin/etiquetas/gestion_etiquetas";
        }
        try {
            etiquetaService.crearOActualizarEtiqueta(etiqueta);
            redirectAttributes.addFlashAttribute("mensajeExito", "Etiqueta creada/actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/admin/etiquetas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarEtiqueta(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        Etiqueta etiqueta = etiquetaService.obtenerEtiquetaPorId(id)
                .orElse(null);
        if (etiqueta == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Etiqueta no encontrada.");
            return "redirect:/admin/etiquetas";
        }
        model.addAttribute("etiqueta", etiqueta);
        model.addAttribute("etiquetas", etiquetaService.obtenerTodasLasEtiquetas());
        return "admin/etiquetas/gestion_etiquetas";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminarEtiqueta(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            etiquetaService.eliminarEtiqueta(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Etiqueta eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar la etiqueta: " + e.getMessage());
        }
        return "redirect:/admin/etiquetas";
    }

    // --- Asignar Etiquetas a Empleado ---

    @GetMapping("/asignar")
    public String mostrarFormularioAsignar(Model model) {
        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();
        List<Etiqueta> todasLasEtiquetas = etiquetaService.obtenerTodasLasEtiquetas();
        model.addAttribute("empleados", empleados);
        model.addAttribute("todasLasEtiquetas", todasLasEtiquetas);
        model.addAttribute("empleadoId", null);
        model.addAttribute("etiquetasSeleccionadasIds", new HashSet<UUID>());
        return "admin/etiquetas/asignar_empleado";
    }

    @GetMapping("/asignar/{empleadoId}")
    public String mostrarFormularioAsignarParaEmpleado(@PathVariable UUID empleadoId, Model model, RedirectAttributes redirectAttributes) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorId(empleadoId).orElse(null);
        if (empleado == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Empleado no encontrado.");
            return "redirect:/admin/etiquetas/asignar";
        }
        List<Etiqueta> todasLasEtiquetas = etiquetaService.obtenerTodasLasEtiquetas();
        Set<UUID> etiquetasActualesIds = etiquetaService.obtenerEtiquetasDeEmpleado(empleadoId).stream()
                .map(Etiqueta::getId)
                .collect(Collectors.toSet());

        model.addAttribute("empleados", empleadoService.obtenerTodosLosEmpleados());
        model.addAttribute("empleadoSeleccionado", empleado);
        model.addAttribute("empleadoId", empleadoId);
        model.addAttribute("todasLasEtiquetas", todasLasEtiquetas);
        model.addAttribute("etiquetasSeleccionadasIds", etiquetasActualesIds);
        return "admin/etiquetas/asignar_empleado";
    }

    @PostMapping("/asignar")
    public String procesarAsignarEtiquetas(@RequestParam UUID empleadoId,
                                           @RequestParam(required = false) Set<UUID> etiquetasIds,
                                           RedirectAttributes redirectAttributes) {
        try {
            etiquetaService.asignarEtiquetasAEmpleado(empleadoId, etiquetasIds == null ? new HashSet<>() : etiquetasIds);
            redirectAttributes.addFlashAttribute("mensajeExito", "Etiquetas asignadas/actualizadas correctamente al empleado.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/admin/etiquetas/asignar/" + empleadoId;
    }

    // --- Etiquetado Masivo ---
    @GetMapping("/masivo")
    public String mostrarFormularioEtiquetadoMasivo(Model model) {
        model.addAttribute("todosLosEmpleados", empleadoService.obtenerTodosLosEmpleados());
        model.addAttribute("todasLasEtiquetas", etiquetaService.obtenerTodasLasEtiquetas());
        return "admin/etiquetas/etiquetado_masivo";
    }

    @PostMapping("/masivo")
    public String procesarEtiquetadoMasivo(@RequestParam List<UUID> empleadosIds,
                                           @RequestParam(name = "etiquetasAsignadasIds", required = false) Set<UUID> etiquetasAsignadasIds,
                                           RedirectAttributes redirectAttributes) {
        try {
            if (empleadosIds == null || empleadosIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", "Debe seleccionar al menos un empleado.");
                return "redirect:/admin/etiquetas/masivo";
            }

            Set<UUID> etiquetasParaAsignar = (etiquetasAsignadasIds == null) ? new HashSet<>() : etiquetasAsignadasIds;

            for (UUID empleadoId : empleadosIds) {
                etiquetaService.asignarEtiquetasAEmpleado(empleadoId, etiquetasParaAsignar);
            }
            redirectAttributes.addFlashAttribute("mensajeExito", "Etiquetas aplicadas masivamente a los empleados seleccionados.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al procesar el etiquetado masivo: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ocurrió un error inesperado durante el etiquetado masivo.");
            // Loggear el error e.printStackTrace(); o usar un logger de SLF4J
            System.err.println("Error en procesarEtiquetadoMasivo: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/etiquetas/masivo";
    }

    // --- API para búsqueda de etiquetas (autocompletado) ---
    @GetMapping("/buscar-ajax")
    @ResponseBody
    public List<Etiqueta> buscarEtiquetasAjax(@RequestParam("term") String term) {
        return etiquetaService.buscarEtiquetasPorNombre(term);
    }
}