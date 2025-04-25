package com.equipo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class LineaNomina {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Nomina nomina;

    private String concepto;
    private BigDecimal importe;
}