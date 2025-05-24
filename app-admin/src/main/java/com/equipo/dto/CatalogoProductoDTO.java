package com.equipo.dto;

import com.equipo.dto.ProductoImportDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogoProductoDTO {
    private String proveedor; // [cite: 7]
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaEnvioCatalogo; // [cite: 7]
    private List<ProductoImportDTO> productos; // [cite: 7]
}