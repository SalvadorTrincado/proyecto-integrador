package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "nominas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nomina {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fechaInicioPeriodo;

    @Column(nullable = false)
    private LocalDate fechaFinPeriodo;

    // Información de la empresa (snapshot en el momento de la nómina)
    @Column(nullable = false)
    private String nombreEmpresa;

    @Column(nullable = false)
    private String cifEmpresa;

    private String direccionEmpresa; // Opcional

    // Información del empleado (snapshot en el momento de la nómina)
    @Column(nullable = false)
    private String nombreCompletoEmpleado;

    @Column(nullable = false)
    private String identificacionEmpleado; // DNI/NIE/Pasaporte

    private String direccionEmpleado; // Opcional

    @Column(nullable = false)
    private String puestoProfesionalEmpleado;

    @Column(nullable = false)
    private String departamentoEmpleado;

    @Column(nullable = false)
    private LocalDate fechaAltaEmpleado;


    // Totales calculados
    @Column(precision = 10, scale = 2)
    private BigDecimal totalDevengos;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalDeducciones;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal netoAPercibir;

    // Campos opcionales para acumulados anuales
    @Column(precision = 10, scale = 2)
    private BigDecimal brutoAcumuladoAnual;

    @Column(precision = 10, scale = 2)
    private BigDecimal retencionesAcumuladasAnual;

    @Column(precision = 10, scale = 2)
    private BigDecimal percibidoAcumuladoAnual;


    @OneToMany(mappedBy = "nomina", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LineaNomina> lineasNomina = new ArrayList<>();

    public void addLineaNomina(LineaNomina linea) {
        if (lineasNomina == null) {
            lineasNomina = new ArrayList<>();
        }
        lineasNomina.add(linea);
        linea.setNomina(this);
    }

    public void removeLineaNomina(LineaNomina linea) {
        if (lineasNomina != null) {
            lineasNomina.remove(linea);
            linea.setNomina(null);
        }
    }
}