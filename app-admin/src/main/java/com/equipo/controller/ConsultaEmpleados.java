package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class ConsultaEmpleados {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/admin/consulta-empleados")
    public String mostrarFormularioConsulta() {
        return "consulta/consulta_empleados";
    }

    @GetMapping("/admin/resultados-empleados")
    public String buscarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Double salarioMaximo,
            Model modelo) {

        List<Empleado> empleados = empleadoRepository.buscarPorParametros(nombre, departamento, salarioMinimo, salarioMaximo);
        modelo.addAttribute("empleados", empleados);
        return "consulta/resultados_empleados";
    }
}
