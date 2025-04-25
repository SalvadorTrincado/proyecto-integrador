package com.equipo.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Colaboracion {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Empleado emisor;

    @ManyToOne
    private Empleado receptor;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    private LocalDateTime fecha;
}