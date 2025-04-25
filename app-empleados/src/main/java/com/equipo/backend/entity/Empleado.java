package com.equipo.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
public class Empleado {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private boolean bajaLogica;

    @OneToMany(mappedBy = "supervisor")
    private List<Empleado> subordinados;

    @ManyToOne
    private Empleado supervisor;

    @OneToMany(mappedBy = "empleado")
    private List<Colaboracion> solicitudes;

    @OneToMany(mappedBy = "empleado")
    private List<Nomina> nominas;

    private Set<Etiqueta> etiquetas = new HashSet<>();
}