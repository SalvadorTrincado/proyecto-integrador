package com.equipo.dto;

import com.equipo.entity.LineaNomina; // Necesitarás importar el enum si está en common
import java.math.BigDecimal;
// import jakarta.validation.constraints.*; // No son necesarias para un DTO de solo lectura
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
public class LineaNominaDTO {

    private String id;
    private String concepto;
    private BigDecimal porcentaje;
    private BigDecimal cantidad;
    private LineaNomina.TipoLinea tipo; // Asume que com.equipo.entity.LineaNomina.TipoLinea es accesible

    // Constructores, Getters y Setters si no usas Lombok
    public LineaNominaDTO() {}

    // (Genera getters y setters para todos los campos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public LineaNomina.TipoLinea getTipo() { return tipo; }
    public void setTipo(LineaNomina.TipoLinea tipo) { this.tipo = tipo; }
}