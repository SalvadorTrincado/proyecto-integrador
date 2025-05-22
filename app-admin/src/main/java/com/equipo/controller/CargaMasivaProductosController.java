package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/productos")
public class CargaMasivaProductosController {

    @GetMapping
    public String mostrarFormularioCarga() {
        return "aplicacion_corporativa/productos/carga_masiva_productos";
    }
}