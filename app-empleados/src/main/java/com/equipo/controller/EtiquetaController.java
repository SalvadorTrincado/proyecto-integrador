/*package com.equipo.controller;

import com.equipo.entity.Etiqueta;
import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import com.equipo.service.EtiquetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/etiquetas")
public class EtiquetaController {

    @Autowired
    private EtiquetaService etiquetaService;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // ----------- FORMULARIO PARA ASIGNAR ETIQUETA -----------

    @GetMapping("/formulario")
    public String mostrarFormularioAsignar() {
        return "aplicacion_corporativa/etiquetar_empleado";
    }

    @PostMapping("/asignar")
    public String asignarEtiqueta(@RequestParam("empleadoId") UUID empleadoId,
                                  @RequestParam("nombreEtiqueta") String nombreEtiqueta) {
        etiquetaService.asignarEtiquetaAEmpleado(empleadoId, nombreEtiqueta);
        return "redirect:/etiquetas/area_personal";
    }

    // ----------- FORMULARIO PARA ELIMINAR ETIQUETAS -----------

    @GetMapping("/eliminar")
    public String mostrarFormularioEliminar() {
        return "aplicacion_corporativa/eliminar_etiquetas";
    }

    @PostMapping("/eliminar")
    public String eliminarEtiquetas(@RequestParam UUID empleadoId,
                                    @RequestParam(required = false) List<String> etiquetas) {
        if (etiquetas != null && !etiquetas.isEmpty()) {
            etiquetaService.eliminarEtiquetasDeEmpleado(empleadoId, etiquetas);
        }
        return "redirect:/etiquetas/area_personal";
    }


    // ----------- CONSULTAS AJAX -----------

    @GetMapping("/buscar")
    @ResponseBody
    public List<String> buscarEtiquetas(@RequestParam("term") String texto) {
        return etiquetaService.buscarPorTexto(texto);
    }

    @GetMapping("/de-empleado")
    @ResponseBody
    public List<String> etiquetasDeEmpleado(@RequestParam UUID id) {
        return empleadoRepository.findById(id)
                .map(empleado -> empleado.getEtiquetas()
                        .stream()
                        .map(Etiqueta::getNombre)
                        .sorted()
                        .toList())
                .orElse(List.of());
    }

    // ----------- ÁREA PERSONAL -----------

    @GetMapping("/area_personal")
    public String areaPersonal() {
        return "aplicacion_corporativa/area_personal";
    }

    // ----------- ETIQUETADO MASIVO -----------

    @GetMapping("/masivo")
    public String mostrarFormularioMasivo() {
        return "aplicacion_corporativa/etiquetado_masivo";
    }

    @PostMapping("/masivo")
    public String procesarEtiquetadoMasivo(@RequestParam("empleados") List<UUID> empleados,
                                           @RequestParam("etiquetas") List<String> etiquetas) {
        etiquetaService.asignarEtiquetasAMultiplesEmpleados(empleados, etiquetas);
        return "redirect:/etiquetas/area_personal";
    }
}*/
