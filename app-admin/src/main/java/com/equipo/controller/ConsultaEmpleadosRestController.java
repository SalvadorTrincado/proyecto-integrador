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

@RestController // Indica que es un controlador REST
@RequestMapping("/api/admin/empleados") // Prefijo para todos los endpoints de este controlador
public class ConsultaEmpleadosRestController {

    @Autowired
    private EmpleadoService empleadoServicio;

    // Endpoint para buscar empleados por un texto general (nombre o apellidos)
    @GetMapping("/buscar")
    public ResponseEntity<List<Empleado>> buscarEmpleados(@RequestParam(required = false) String q) {
        List<Empleado> empleados = empleadoServicio.buscarEmpleados(q);
        return ResponseEntity.ok(empleados);
    }

    // Endpoint para buscar empleados por múltiples criterios (nombre, apellidos, departamento)
    // Ej: /api/admin/empleados/filtrar?nombre=Juan&departamento=IT
    @GetMapping("/filtrar")
    public ResponseEntity<List<Empleado>> filtrarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String departamento) {

        List<Empleado> empleados = empleadoServicio.buscarEmpleadosPorCriterios(nombre, apellidos, departamento);
        return ResponseEntity.ok(empleados);
    }

    // Opcional: Endpoint para obtener todos los empleados (si lo necesitas)
    @GetMapping("/todos")
    public ResponseEntity<List<Empleado>> obtenerTodosEmpleados() {
        List<Empleado> empleados = empleadoServicio.obtenerTodosLosEmpleados();
        return ResponseEntity.ok(empleados);
    }


}