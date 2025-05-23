package com.equipo.dto;

// Importa BigDecimal, LocalDate, List si es necesario
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Usa Lombok o getters/setters manuales
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
public class NominaDetalleDTO {

    private String id;
    private String empleadoId;
    private String nombreCompletoEmpleado;
    private String identificacionEmpleado;
    private String puestoProfesionalEmpleado;
    private String departamentoEmpleado;
    private LocalDate fechaAltaEmpleado;
    private LocalDate fechaInicioPeriodo;
    private LocalDate fechaFinPeriodo;
    private String nombreEmpresa;
    private String cifEmpresa;
    private String direccionEmpresa;
    private List<LineaNominaDTO> lineasNomina;
    private BigDecimal totalDevengos;
    private BigDecimal totalDeducciones;
    private BigDecimal netoAPercibir;
    private BigDecimal brutoAcumuladoAnual;
    private BigDecimal retencionesAcumuladasAnual;
    private BigDecimal percibidoAcumuladoAnual;

    // Constructores, Getters y Setters si no usas Lombok
    public NominaDetalleDTO() {}

    // (Genera getters y setters para todos los campos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(String empleadoId) { this.empleadoId = empleadoId; }
    public String getNombreCompletoEmpleado() { return nombreCompletoEmpleado; }
    public void setNombreCompletoEmpleado(String nombreCompletoEmpleado) { this.nombreCompletoEmpleado = nombreCompletoEmpleado; }
    public String getIdentificacionEmpleado() { return identificacionEmpleado; }
    public void setIdentificacionEmpleado(String identificacionEmpleado) { this.identificacionEmpleado = identificacionEmpleado; }
    public String getPuestoProfesionalEmpleado() { return puestoProfesionalEmpleado; }
    public void setPuestoProfesionalEmpleado(String puestoProfesionalEmpleado) { this.puestoProfesionalEmpleado = puestoProfesionalEmpleado; }
    public String getDepartamentoEmpleado() { return departamentoEmpleado; }
    public void setDepartamentoEmpleado(String departamentoEmpleado) { this.departamentoEmpleado = departamentoEmpleado; }
    public LocalDate getFechaAltaEmpleado() { return fechaAltaEmpleado; }
    public void setFechaAltaEmpleado(LocalDate fechaAltaEmpleado) { this.fechaAltaEmpleado = fechaAltaEmpleado; }
    public LocalDate getFechaInicioPeriodo() { return fechaInicioPeriodo; }
    public void setFechaInicioPeriodo(LocalDate fechaInicioPeriodo) { this.fechaInicioPeriodo = fechaInicioPeriodo; }
    public LocalDate getFechaFinPeriodo() { return fechaFinPeriodo; }
    public void setFechaFinPeriodo(LocalDate fechaFinPeriodo) { this.fechaFinPeriodo = fechaFinPeriodo; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    public String getCifEmpresa() { return cifEmpresa; }
    public void setCifEmpresa(String cifEmpresa) { this.cifEmpresa = cifEmpresa; }
    public String getDireccionEmpresa() { return direccionEmpresa; }
    public void setDireccionEmpresa(String direccionEmpresa) { this.direccionEmpresa = direccionEmpresa; }
    public List<LineaNominaDTO> getLineasNomina() { return lineasNomina; }
    public void setLineasNomina(List<LineaNominaDTO> lineasNomina) { this.lineasNomina = lineasNomina; }
    public BigDecimal getTotalDevengos() { return totalDevengos; }
    public void setTotalDevengos(BigDecimal totalDevengos) { this.totalDevengos = totalDevengos; }
    public BigDecimal getTotalDeducciones() { return totalDeducciones; }
    public void setTotalDeducciones(BigDecimal totalDeducciones) { this.totalDeducciones = totalDeducciones; }
    public BigDecimal getNetoAPercibir() { return netoAPercibir; }
    public void setNetoAPercibir(BigDecimal netoAPercibir) { this.netoAPercibir = netoAPercibir; }
    public BigDecimal getBrutoAcumuladoAnual() { return brutoAcumuladoAnual; }
    public void setBrutoAcumuladoAnual(BigDecimal brutoAcumuladoAnual) { this.brutoAcumuladoAnual = brutoAcumuladoAnual; }
    public BigDecimal getRetencionesAcumuladasAnual() { return retencionesAcumuladasAnual; }
    public void setRetencionesAcumuladasAnual(BigDecimal retencionesAcumuladasAnual) { this.retencionesAcumuladasAnual = retencionesAcumuladasAnual; }
    public BigDecimal getPercibidoAcumuladoAnual() { return percibidoAcumuladoAnual; }
    public void setPercibidoAcumuladoAnual(BigDecimal percibidoAcumuladoAnual) { this.percibidoAcumuladoAnual = percibidoAcumuladoAnual; }
}