package com.equipo.controller;

import com.equipo.backend.dto.EmpleadoRegistroDTO;
import com.equipo.backend.entity.Empleado;
import com.equipo.backend.service.EmpleadoService;
import com.equipo.model.dto.DatosContactoDTO;
import com.equipo.model.dto.DatosEconomicosDTO;
import com.equipo.model.dto.DatosPersonalesDTO;
import com.equipo.model.dto.DatosProfesionalesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroEmpleadoResumenController {

    @Autowired
    private EmpleadoService empleadoService;


    @GetMapping("/registro/empleado/resumen")
    public String mostrarResumen(HttpSession session, Model model) {
        model.addAttribute("datosPersonales", session.getAttribute("datosPersonales"));
        model.addAttribute("datosContacto", session.getAttribute("datosContacto"));
        model.addAttribute("datosProfesionales", session.getAttribute("datosProfesionales"));
        model.addAttribute("datosEconomicos", session.getAttribute("datosEconomicos"));
        return "registro/empleado/paso5";
    }

    @PostMapping("/registro/empleado/resumen")
    public String guardarEmpleado(HttpSession session) {
        DatosPersonalesDTO personales = (DatosPersonalesDTO) session.getAttribute("datosPersonales");
        DatosContactoDTO contacto = (DatosContactoDTO) session.getAttribute("datosContacto");
        DatosProfesionalesDTO profesionales = (DatosProfesionalesDTO) session.getAttribute("datosProfesionales");
        DatosEconomicosDTO economicos = (DatosEconomicosDTO) session.getAttribute("datosEconomicos");

        EmpleadoRegistroDTO empleadoDTO = new EmpleadoRegistroDTO(
                personales.getNombre(),
                personales.getApellidos(),
                contacto.getTelefonoMovil(),
                contacto.getTipoDocumento(),
                contacto.getDocumento(),
                profesionales.getDepartamento(),
                economicos.getCategoriaProfesional(),
                economicos.getTipoContrato(),
                economicos.getNumeroCuenta(),
                economicos.getSalarioBaseMensual().doubleValue(),
                economicos.getComplementoMensual().doubleValue(),
                economicos.getFechaIncorporacion()
        );

        empleadoService.registrarEmpleado(empleadoDTO);

        session.invalidate(); // limpiar la sesión tras guardar

        return "redirect:/registro/confirmacion"; // o cualquier página final
    }

}
