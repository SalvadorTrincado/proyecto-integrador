package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin") // Define la ruta base para este controlador REST
public class EmpleadoRestController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/empleados")
    public ResponseEntity<List<Empleado>> buscarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Double salarioMaximo) {

        // Utiliza el método existente en tu repositorio
        List<Empleado> empleados = empleadoRepository.buscarPorParametros(nombre, departamento, salarioMinimo, salarioMaximo); //
        return ResponseEntity.ok(empleados);
    }
}
