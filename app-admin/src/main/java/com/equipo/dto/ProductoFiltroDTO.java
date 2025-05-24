package com.equipo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductoFiltroDTO {
    private String descripcion; // [cite: 40]
    private Long proveedorId; // ID del proveedor seleccionado [cite: 42]
    private List<String> nombresCategorias; // Lista de nombres de categorías seleccionadas [cite: 44]
    private Boolean esPerecedero; // [cite: 46]
    // Para paginación y ordenación, se usará Pageable de Spring
}