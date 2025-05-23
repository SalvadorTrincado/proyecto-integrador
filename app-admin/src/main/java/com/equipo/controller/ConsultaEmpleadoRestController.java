package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/empleados")
public class ConsultaEmpleadoRestController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/consultar")
    public ResponseEntity<List<Empleado>> consultarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Double salarioMaximo
    ) {
        List<Empleado> empleados;
        if (isNotBlank(nombre) || isNotBlank(departamento) || salarioMinimo != null || salarioMaximo != null) {
            empleados = empleadoService.buscarEmpleadosPorParametros(
                    isNotBlank(nombre) ? nombre : null,
                    isNotBlank(departamento) ? departamento : null,
                    salarioMinimo,
                    salarioMaximo
            );
        } else {
            empleados = empleadoService.obtenerTodosLosEmpleados();
        }
        return ResponseEntity.ok(empleados);
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
