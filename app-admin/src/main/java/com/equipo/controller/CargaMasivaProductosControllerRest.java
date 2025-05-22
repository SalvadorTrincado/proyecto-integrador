package com.equipo.controller;

import com.equipo.service.ProductoCargaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/productos")
public class CargaMasivaProductosControllerRest {

    @Autowired
    private ProductoCargaService productoCargaService;

    @PostMapping("/carga-masiva")
    @ResponseBody
    public ResponseEntity<?> cargarProductos(@RequestParam("archivo") MultipartFile archivo) {
        try {
            productoCargaService.cargarDesdeCSV(archivo);
            return ResponseEntity.ok("Carga completada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar productos: " + e.getMessage());
        }
    }
}
