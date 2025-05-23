package com.equipo.controller;

import com.equipo.entity.Producto;
import com.equipo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequestMapping("/admin/productos")
public class DevolverProductos {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/todos")
    public ResponseEntity<List<Producto>> listarTodosProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }
}