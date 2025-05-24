package com.equipo.controller;

import com.equipo.dto.ProductoResponseDTO;
import com.equipo.entity.Producto;
import com.equipo.entity.Categoria;
import com.equipo.repository.ProductoRepository;
import com.equipo.service.ProductoCargaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional; // Asegúrate de importar esta
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/productos")
public class CargaMasivaProductosControllerRest {

    @Autowired
    private ProductoCargaService productoCargaService;
    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping("/carga-masiva-json-api")
    public ResponseEntity<?> cargarProductosJsonApi(@RequestParam("archivoJson") MultipartFile archivoJson) {
        if (archivoJson.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Por favor, seleccione un archivo JSON para cargar."));
        }
        String contentType = archivoJson.getContentType();
        if (contentType == null || (!contentType.equals("application/json") && !contentType.equals("text/plain"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Formato de archivo no válido. Por favor, suba un archivo JSON."));
        }
        try {
            String mensaje = productoCargaService.cargarDesdeJson(archivoJson);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno procesando el archivo: " + e.getMessage()));
        }
    }

    @GetMapping("/consultar")
    public ResponseEntity<List<ProductoResponseDTO>> consultarProductos(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) String categoriaNombre,
            @RequestParam(required = false) Boolean esPerecedero) {
        try {
            List<Producto> productosEntidad = productoRepository.findListByFiltrosCompletos(
                    (descripcion != null && !descripcion.isEmpty()) ? descripcion : null,
                    proveedorId,
                    (categoriaNombre != null && !categoriaNombre.isEmpty()) ? categoriaNombre : null,
                    esPerecedero
            );

            if (productosEntidad == null || productosEntidad.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<ProductoResponseDTO> productosDto = productosEntidad.stream()
                    .map(this::convertToProductoResponseDto) // Usar el método de mapeo
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productosDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // NUEVO: Endpoint para obtener detalles de un producto
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        Optional<ProductoResponseDTO> productoDTOOpt = productoCargaService.obtenerProductoDTOPorId(id);
        if (productoDTOOpt.isPresent()) {
            return ResponseEntity.ok(productoDTOOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Producto no encontrado con ID: " + id));
        }
    }

    // NUEVO: Endpoint para eliminar un producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        System.out.println("API: Solicitud para eliminar producto con ID: " + id); // NUEVA LÍNEA PARA DEBUG
        try {
            productoCargaService.eliminarProductoPorId(id); // O productoCargaService si es el que estás usando
            return ResponseEntity.ok(Map.of("mensaje", "Producto con ID " + id + " eliminado correctamente."));
        } catch (EntityNotFoundException e) {
            System.err.println("API: EntityNotFoundException al eliminar producto ID " + id + ": " + e.getMessage()); // DEBUG
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("API: Exception general al eliminar producto ID " + id + ": " + e.getMessage()); // DEBUG
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al eliminar el producto: " + e.getMessage()));
        }
    }

    // NUEVO: Endpoint para eliminar todos los productos
    @DeleteMapping("/eliminar-todos")
    public ResponseEntity<?> eliminarTodosLosProductos() {
        try {
            long cantidadProductosAntes = productoRepository.count();
            if (cantidadProductosAntes == 0) {
                return ResponseEntity.ok(Map.of("mensaje", "No había productos para eliminar."));
            }
            productoCargaService.eliminarTodosLosProductos();
            long cantidadProductosDespues = productoRepository.count();
            return ResponseEntity.ok(Map.of("mensaje", "Todos los productos (" + cantidadProductosAntes + ") han sido eliminados. Quedan " + cantidadProductosDespues + " productos."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al eliminar todos los productos: " + e.getMessage()));
        }
    }

    private ProductoResponseDTO convertToProductoResponseDto(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setMarca(producto.getMarca());
        dto.setUnidades(producto.getUnidades());
        dto.setFechaFabricacion(producto.getFechaFabricacion());
        dto.setFechaAlta(producto.getFechaAlta());
        dto.setValoracion(producto.getValoracion());
        dto.setEsPerecedero(producto.getEsPerecedero());

        if (producto.getProveedor() != null) {
            dto.setProveedorId(producto.getProveedor().getId());
            dto.setProveedorNombre(producto.getProveedor().getNombre());
        }

        if (producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
            dto.setNombresCategorias(producto.getCategorias().stream()
                    .map(Categoria::getNombre)
                    .collect(Collectors.toSet()));
        }

        dto.setTitulo(producto.getTitulo());
        dto.setAutor(producto.getAutor());
        dto.setEditorial(producto.getEditorial());
        dto.setTapa(producto.getTapa());
        dto.setNumeroPaginas(producto.getNumeroPaginas());
        dto.setSegundaMano(producto.getSegundaMano());
        dto.setDimensionAncho(producto.getDimensionAncho());
        dto.setDimensionProfundo(producto.getDimensionProfundo());
        dto.setDimensionAlto(producto.getDimensionAlto());
        if (producto.getColores() != null) {
            dto.setColores(producto.getColores());
        }
        dto.setTalla(producto.getTalla());
        dto.setMaterial(producto.getMaterial());

        return dto;
    }
}