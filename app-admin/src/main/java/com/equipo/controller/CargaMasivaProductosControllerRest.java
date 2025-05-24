package com.equipo.controller;

import com.equipo.service.ProductoCargaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // Puede seguir siendo @Controller si también sirve vistas HTML
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller // Si va a redirigir a una vista Thymeleaf. Si es solo API REST, usar @RestController.
@RequestMapping("/admin/productos")
public class CargaMasivaProductosControllerRest { // Renombrado para evitar conflicto con el MVC si lo hay.

    @Autowired
    private ProductoCargaService productoCargaService;

    // Este es el endpoint que procesará la subida del archivo JSON desde el formulario.
    @PostMapping("/carga-masiva-json") // Nueva ruta para evitar colisión con la de CSV si se mantiene.
    public String cargarProductosJson(@RequestParam("archivoJson") MultipartFile archivoJson,
                                      RedirectAttributes redirectAttributes) { // [cite: 11, 13]
        if (archivoJson.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorProductoCarga", "Por favor, seleccione un archivo JSON para cargar.");
            return "redirect:/admin/productos/carga"; // Redirige de vuelta al formulario de carga
        }

        // Validar que es un archivo JSON por tipo MIME (opcional pero recomendado)
        String contentType = archivoJson.getContentType();
        if (contentType == null || (!contentType.equals("application/json") && !contentType.equals("text/plain"))) {
            // text/plain a veces es usado para JSON
            redirectAttributes.addFlashAttribute("errorProductoCarga",
                    "Formato de archivo no válido. Por favor, suba un archivo JSON.");
            return "redirect:/admin/productos/carga";
        }


        try {
            String mensaje = productoCargaService.cargarDesdeJson(archivoJson);
            redirectAttributes.addFlashAttribute("exitoProductoCarga", mensaje); // [cite: 17]
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorProductoCarga", e.getMessage()); // [cite: 14, 15, 16]
        } catch (Exception e) {
            e.printStackTrace(); // Loguear el error completo
            redirectAttributes.addFlashAttribute("errorProductoCarga",
                    "Error interno procesando el archivo: " + e.getMessage());
        }
        return "redirect:/admin/productos/carga"; // Redirige de vuelta al formulario de carga
    }

    // Mantener el método antiguo de CSV si es necesario, o eliminarlo.
    // @PostMapping("/carga-masiva") // Este era el de CSV
    // @ResponseBody
    // public ResponseEntity<?> cargarProductos(@RequestParam("archivo") MultipartFile archivo) {
    //     try {
    //         // productoCargaService.cargarDesdeCSV(archivo); //Llamada al método CSV
    //         return ResponseEntity.ok("Carga CSV completada correctamente.");
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Error al cargar productos desde CSV: " + e.getMessage());
    //     }
    // }
}