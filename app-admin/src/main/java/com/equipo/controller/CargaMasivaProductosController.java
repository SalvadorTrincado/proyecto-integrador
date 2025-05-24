package com.equipo.controller;

import com.equipo.dto.ProductoResponseDTO; // Importar DTO
import com.equipo.entity.Categoria;
import com.equipo.entity.Proveedor;
import com.equipo.repository.CategoriaRepository;
import com.equipo.repository.ProveedorRepository;
import com.equipo.service.ProductoCargaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Añadir PathVariable
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/productos")
public class CargaMasivaProductosController {

    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoCargaService productoCargaService;

    @Autowired
    public CargaMasivaProductosController(ProveedorRepository proveedorRepository,
                                          CategoriaRepository categoriaRepository,
                                          ProductoCargaService productoCargaService) { // Añadir al constructor
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoCargaService = productoCargaService;
    }

    @GetMapping("/gestion-dinamica")
    public String mostrarPaginaGestionDinamica(Model model) {
        List<Proveedor> todosLosProveedores = proveedorRepository.findAll();
        List<Categoria> todasLasCategorias = categoriaRepository.findAll();

        model.addAttribute("proveedores", todosLosProveedores);
        model.addAttribute("categorias", todasLasCategorias);

        return "aplicacion_corporativa/productos/gestion_productos_dinamica";
    }

    @PostMapping("/carga-masiva-json-mvc")
    public String cargarProductosJsonMvc(@RequestParam("archivoJson") MultipartFile archivoJson,
                                         RedirectAttributes redirectAttributes) {
        if (archivoJson.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorProductoCarga", "Por favor, seleccione un archivo JSON para cargar.");
            return "redirect:/admin/productos/gestion-dinamica";
        }
        String contentType = archivoJson.getContentType();
        if (contentType == null || (!contentType.equals("application/json") && !contentType.equals("text/plain"))) {
            redirectAttributes.addFlashAttribute("errorProductoCarga", "Formato de archivo no válido. Por favor, suba un archivo JSON.");
            return "redirect:/admin/productos/gestion-dinamica";
        }
        try {
            String mensaje = productoCargaService.cargarDesdeJson(archivoJson);
            redirectAttributes.addFlashAttribute("exitoProductoCarga", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorProductoCarga", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorProductoCarga", "Error interno procesando el archivo: " + e.getMessage());
        }
        return "redirect:/admin/productos/gestion-dinamica";
    }

    @GetMapping("/carga")
    public String redirigirACargaDinamica() {
        return "redirect:/admin/productos/gestion-dinamica";
    }

    // NUEVO: Endpoint MVC para ver detalles de un producto
    @GetMapping("/detalle/{id}")
    public String mostrarDetalleProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ProductoResponseDTO> productoDTOOpt = productoCargaService.obtenerProductoDTOPorId(id);
        if (productoDTOOpt.isPresent()) {
            model.addAttribute("producto", productoDTOOpt.get());
            return "aplicacion_corporativa/productos/detalle_producto"; // Nueva plantilla HTML
        } else {
            redirectAttributes.addFlashAttribute("errorGlobal", "Producto no encontrado con ID: " + id);
            return "redirect:/admin/productos/gestion-dinamica";
        }
    }
}