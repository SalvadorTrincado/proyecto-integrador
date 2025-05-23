package com.equipo.controller;

import com.equipo.entity.Administrador;
import com.equipo.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class AdministradorRest {

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping("/consulta-administrador")
    public ResponseEntity<List<Administrador>> listarTodosEmpleados() {
        List<Administrador> administradores = administradorRepository.findAll();
        return ResponseEntity.ok(administradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> getAdministradorById(@PathVariable UUID id) {
        Optional<Administrador> admin = administradorRepository.findById(id);
        return admin.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
