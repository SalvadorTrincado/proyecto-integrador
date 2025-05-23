package com.equipo.controller;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DevolverEmpleados {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/todosEmpleados")
    public ResponseEntity<List<Empleado>> listarTodosEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        return ResponseEntity.ok(empleados);
    }


}