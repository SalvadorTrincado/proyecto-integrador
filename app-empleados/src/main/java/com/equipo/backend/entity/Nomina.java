package com.equipo.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Nomina {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Empleado empleado;

    private LocalDate periodo;
    private BigDecimal total;

    @OneToMany(mappedBy = "nomina", cascade = CascadeType.ALL)
    private List<LineaNomina> lineas;
}