package com.equipo.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Colaboracion {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emisor_id")
    private Empleado emisor;

    @ManyToOne
    @JoinColumn(name = "receptor_id")
    private Empleado receptor;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    private LocalDateTime fecha;
}