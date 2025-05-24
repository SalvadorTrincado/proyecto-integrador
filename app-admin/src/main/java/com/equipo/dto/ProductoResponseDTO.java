package com.equipo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String descripcion;
    private Double precio;
    private String marca;
    private Integer unidades;
    private LocalDate fechaFabricacion;
    private LocalDate fechaAlta;
    private Integer valoracion;
    private Boolean esPerecedero;

    // Información simplificada del Proveedor
    private UUID proveedorId;
    private String proveedorNombre;

    // Información simplificada de las Categorías
    private Set<String> nombresCategorias;

    // Campos específicos que podrías querer incluir
    private String titulo;
    private String autor;
    private String editorial;
    private String tapa;
    private Integer numeroPaginas;
    private Boolean segundaMano;
    private Double dimensionAncho;
    private Double dimensionProfundo;
    private Double dimensionAlto;
    private List<String> colores;
    private String talla;
    private String material;
}
