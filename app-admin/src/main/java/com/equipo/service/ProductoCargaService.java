package com.equipo.service;

import com.equipo.dto.CatalogoProductoDTO;
import com.equipo.dto.ProductoImportDTO;
import com.equipo.dto.ProductoResponseDTO;
import com.equipo.entity.Categoria;
import com.equipo.entity.Producto;
import com.equipo.entity.Proveedor;
import com.equipo.repository.CategoriaRepository;
import com.equipo.repository.ProductoRepository;
import com.equipo.repository.ProveedorRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoCargaService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductoCargaService(ProductoRepository productoRepository,
                                ProveedorRepository proveedorRepository,
                                CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Para LocalDate
    }

    @Transactional
    public String cargarDesdeJson(MultipartFile archivo) throws Exception {
        CatalogoProductoDTO catalogo;
        try (InputStream inputStream = archivo.getInputStream()) {
            catalogo = objectMapper.readValue(inputStream, CatalogoProductoDTO.class);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Error de sintaxis en el archivo JSON en la línea " +
                    e.getLocation().getLineNr() + ", columna " + e.getLocation().getColumnNr() +
                    ". No se importará ningún producto. Detalle: " + e.getOriginalMessage()); // [cite: 14]
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo JSON: " + e.getMessage(), e);
        }

        // Validaciones de lógica de negocio a nivel de catálogo
        if (catalogo.getProveedor() == null || catalogo.getProveedor().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor en el catálogo no puede estar vacío."); // [cite: 15, 31]
        }
        Proveedor proveedor = proveedorRepository.findByNombre(catalogo.getProveedor())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor '" + catalogo.getProveedor() +
                        "' no encontrado en la base de datos. El catálogo debe pertenecer a un proveedor existente.")); // [cite: 15, 20, 31]

        if (catalogo.getFechaEnvioCatalogo() == null || catalogo.getFechaEnvioCatalogo().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de envío del catálogo debe ser una fecha pasada o presente."); // [cite: 15, 31]
        }

        if (catalogo.getProductos() == null || catalogo.getProductos().isEmpty()) {
            throw new IllegalArgumentException("El catálogo no contiene productos para importar."); // [cite: 15]
        }

        int productosImportados = 0;
        int productosActualizados = 0;

        for (int i = 0; i < catalogo.getProductos().size(); i++) {
            ProductoImportDTO productoDto = catalogo.getProductos().get(i);
            try {
                validarProductoDto(productoDto, catalogo.getFechaEnvioCatalogo()); // [cite: 15, 16]

                Optional<Producto> productoExistenteOpt = productoRepository.findByDescripcionAndProveedor(productoDto.getDescripcion(), proveedor);

                if (productoExistenteOpt.isPresent()) { // Producto ya existe [cite: 27]
                    Producto productoExistente = productoExistenteOpt.get();
                    productoExistente.setPrecio(productoDto.getPrecio());
                    productoExistente.setUnidades((productoExistente.getUnidades() != null ? productoExistente.getUnidades() : 0) + productoDto.getUnidades());
                    // Opcional: actualizar fechaFabricacion si unidades era 0 [cite: 28]
                    if (productoExistente.getUnidades() == 0 && productoDto.getFechaFabricacion() != null) {
                        productoExistente.setFechaFabricacion(productoDto.getFechaFabricacion());
                    }
                    // Actualizar otros campos comunes si es necesario según la lógica de negocio
                    productoRepository.save(productoExistente);
                    productosActualizados++;
                } else { // Nuevo producto
                    Producto nuevoProducto = new Producto();
                    nuevoProducto.setProveedor(proveedor);
                    mapDtoToProducto(productoDto, nuevoProducto, catalogo.getFechaEnvioCatalogo());
                    productoRepository.save(nuevoProducto);
                    productosImportados++;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error en el producto #" + (i + 1) + " (Descripción: " +
                        (productoDto.getDescripcion() != null ? productoDto.getDescripcion() : "N/A") + "): " +
                        e.getMessage() + ". No se importará ningún producto del catálogo."); // [cite: 15, 16]
            }
        }
        return "Importación completada. Productos nuevos: " + productosImportados + ". Productos actualizados: " +
                productosActualizados + "."; // [cite: 17]
    }

    private void validarProductoDto(ProductoImportDTO dto, LocalDate fechaEnvioCatalogo) {
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria."); // [cite: 31]
        }
        if (dto.getPrecio() == null || dto.getPrecio() <= 0.0) {
            throw new IllegalArgumentException("El precio debe ser un número real mayor que 0.0."); // [cite: 31]
        }
        if (dto.getCategorias() == null || dto.getCategorias().isEmpty()) {
            throw new IllegalArgumentException("Debe especificarse al menos una categoría."); // [cite: 33]
        }
        if (dto.getUnidades() == null || dto.getUnidades() <= 0) {
            throw new IllegalArgumentException("Las unidades deben ser un número entero mayor que 0."); // [cite: 33]
        }
        if (dto.getFechaFabricacion() == null || dto.getFechaFabricacion().isAfter(fechaEnvioCatalogo.minusDays(1))) {
            throw new IllegalArgumentException("La fecha de fabricación debe ser pasada y anterior a la fecha de envío del catálogo."); // [cite: 33]
        }
        // Validaciones para campos específicos (simplificado) [cite: 34, 35]
        if ("Muebles".equalsIgnoreCase(dto.getCategorias().get(0))) { // Asumiendo que la primera es la principal
            if (dto.getDimensiones() != null) {
                if (dto.getDimensiones().get("ancho") <= 0 || dto.getDimensiones().get("profundo") <= 0 || dto.getDimensiones().get("alto") <= 0) {
                    throw new IllegalArgumentException("Las dimensiones del mueble deben ser mayores que 0.");
                }
            }
        }
        if ("Libro".equalsIgnoreCase(dto.getCategorias().get(0))) {
            if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
                throw new IllegalArgumentException("El título del libro es obligatorio.");
            }
            if (dto.getAutor() == null || dto.getAutor().trim().isEmpty()) {
                throw new IllegalArgumentException("El autor del libro es obligatorio.");
            }
            if (dto.getNumeroPaginas() != null && dto.getNumeroPaginas() <=0) {
                throw new IllegalArgumentException("El número de páginas debe ser mayor a cero.");
            }
        }
    }

    private void mapDtoToProducto(ProductoImportDTO dto, Producto producto, LocalDate fechaEnvioCatalogo) {
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setMarca(dto.getMarca()); // Marca es opcional [cite: 31]
        producto.setUnidades(dto.getUnidades());
        producto.setFechaFabricacion(dto.getFechaFabricacion());
        producto.setEsPerecedero(dto.getEsPerecedero()); // esPerecedero es opcional [cite: 33]

        // Manejo de categorías [cite: 22]
        Set<Categoria> categoriasEntidad = new HashSet<>();
        if (dto.getCategorias() != null) {
            for (String nombreCategoria : dto.getCategorias()) {
                Categoria cat = categoriaRepository.findByNombre(nombreCategoria)
                        .orElseGet(() -> categoriaRepository.save(new Categoria(nombreCategoria)));
                categoriasEntidad.add(cat);
            }
        }
        producto.setCategorias(categoriasEntidad);

        // Mapeo de campos específicos (ejemplos)
        if ("Muebles".equalsIgnoreCase(dto.getCategorias().get(0)) && dto.getDimensiones() != null) {
            producto.setDimensionAncho(dto.getDimensiones().get("ancho") != null ? dto.getDimensiones().get("ancho").doubleValue() : null);
            producto.setDimensionProfundo(dto.getDimensiones().get("profundo") != null ? dto.getDimensiones().get("profundo").doubleValue() : null);
            producto.setDimensionAlto(dto.getDimensiones().get("alto") != null ? dto.getDimensiones().get("alto").doubleValue() : null);
            if(dto.getColores() != null) producto.setColores(new ArrayList<>(dto.getColores()));
        } else if ("Libro".equalsIgnoreCase(dto.getCategorias().get(0))) {
            producto.setTitulo(dto.getTitulo());
            producto.setAutor(dto.getAutor());
            producto.setEditorial(dto.getEditorial());
            producto.setTapa(dto.getTapa());
            producto.setNumeroPaginas(dto.getNumeroPaginas());
            producto.setSegundaMano(dto.getSegundaMano());
        }
        // producto.setFechaAlta se establece con @PrePersist [cite: 25]
        // producto.setValoracion por defecto es 0 [cite: 26]
    }

    @Transactional(readOnly = true)
    public Optional<ProductoResponseDTO> obtenerProductoDTOPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::convertToProductoResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoEntidadPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public void eliminarProductoPorId(Long id) {
        System.out.println("Servicio: Intentando eliminar producto con ID: " + id); // DEBUG
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    System.err.println("Servicio: Producto no encontrado en BD con ID: " + id); // DEBUG
                    return new EntityNotFoundException("Producto no encontrado con ID: " + id + " (verificado con findById). No se puede eliminar.");
                });
        System.out.println("Servicio: Producto encontrado, procediendo a eliminar: " + producto.getDescripcion()); // DEBUG
        productoRepository.delete(producto);
        System.out.println("Servicio: Producto con ID " + id + " eliminado (después de delete)."); // DEBUG
    }

    @Transactional
    public void eliminarTodosLosProductos() {
        // Similar al caso anterior, JPA maneja las tablas de join.
        productoRepository.deleteAll();
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