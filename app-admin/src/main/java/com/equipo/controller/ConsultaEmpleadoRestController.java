package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/empleados")
public class ConsultaEmpleadoRestController {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaEmpleadoRestController.class);

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/consultar")
    public ResponseEntity<List<Empleado>> consultarEmpleados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Double salarioMaximo
    ) {
        logger.info("API /consultar RECIBIDO: nombre='{}', departamento='{}', salarioMinimo={}, salarioMaximo={}",
                nombre, departamento, salarioMinimo, salarioMaximo);

        List<Empleado> empleados;
        boolean tieneFiltros = isNotBlank(nombre) || isNotBlank(departamento) || salarioMinimo != null || salarioMaximo != null;

        if (tieneFiltros) {
            logger.info("Buscando empleados CON FILTROS.");
            empleados = empleadoService.buscarEmpleadosPorParametros(
                    isNotBlank(nombre) ? nombre : null,
                    isNotBlank(departamento) ? departamento : null,
                    salarioMinimo,
                    salarioMaximo
            );
        } else {
            logger.info("Buscando TODOS los empleados (SIN filtros).");
            empleados = empleadoService.obtenerTodosLosEmpleados();
        }

        if (empleados != null) {
            logger.info("Número de empleados ENCONTRADOS: {}", empleados.size());
            // Loguear IDs si son pocos, o solo los primeros N si son muchos
            // logger.debug("IDs Encontrados: {}", empleados.stream().map(Empleado::getId).collect(Collectors.toList()));
        } else {
            logger.warn("La lista de empleados es NULL después de la consulta.");
        }

        return ResponseEntity.ok(empleados);
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}