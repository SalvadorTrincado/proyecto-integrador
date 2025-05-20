package com.equipo.service;

import com.equipo.entity.Producto;
import com.equipo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoCargaService {

    @Autowired
    private ProductoRepository productoRepository;

    public void cargarDesdeCSV(MultipartFile archivo) throws Exception {
        List<Producto> productos = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] campos = linea.split(";");
                if (campos.length < 4) continue;

                Producto producto = new Producto();
                producto.setNombre(campos[0]);
                producto.setDescripcion(campos[1]);
                producto.setPrecio(Double.parseDouble(campos[2]));
                producto.setStock(Integer.parseInt(campos[3]));

                productos.add(producto);
            }
        }

        productoRepository.saveAll(productos);
    }
}
