package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsultaEmpleados {

    @GetMapping("/admin/consulta-empleados")
    public String mostrarFormularioConsulta() {
        return "consulta/consulta_empleados"; // Sirve la página HTML modificada
    }

}
