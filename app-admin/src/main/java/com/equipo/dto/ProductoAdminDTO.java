package com.equipo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class ProductoAdminDTO {
    private Long id;
    private String descripcion;
    private Double precio;
    private String marca;
    private Integer unidades;
    private LocalDate fechaFabricacion;
    private LocalDate fechaAlta;
    private Integer valoracion;
    private Boolean esPerecedero;

    private String proveedorNombre; // Nombre del proveedor
    private Long proveedorId;

    private Set<String> nombresCategorias; // Nombres de las categorías

    // Campos específicos (replicar los de ProductoImportDTO o los de la entidad Producto)
    private String titulo;
    private String autor;
    // ... otros campos específicos
    private Double dimensionAncho;
    private Double dimensionProfundo;
    private Double dimensionAlto;
    private List<String> colores;
    private String talla;
    private String material;
}