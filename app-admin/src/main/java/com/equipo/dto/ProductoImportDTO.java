package com.equipo.dto; // Crear paquete si no existe

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos no mapeados explícitamente
public class ProductoImportDTO {
    // Campos comunes del JSON
    private String descripcion; // [cite: 7]
    private Double precio; // [cite: 7]
    private String marca; // [cite: 7]
    private List<String> categorias; // [cite: 7]
    private Boolean esPerecedero; // "true" o "false" en JSON, se mapeará a Boolean [cite: 7]
    private Integer unidades; // [cite: 7]
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaFabricacion; // [cite: 7]

    // Campos específicos (ejemplos del JSON)
    // Para Mueble
    private Map<String, Integer> dimensiones; // {ancho, profundo, alto} [cite: 7]
    private List<String> colores; // [cite: 7]

    // Para Libro
    private String titulo; // [cite: 8]
    private String autor; // [cite: 8]
    private String editorial; // [cite: 8]
    private String tapa; // [cite: 8]
    private Integer numeroPaginas; // [cite: 8]
    private Boolean segundaMano; // "true" o "false" en JSON [cite: 8]

    // Atributos adicionales no estándar pueden ser capturados con @JsonAnySetter
    private Map<String, Object> atributosAdicionales;
}