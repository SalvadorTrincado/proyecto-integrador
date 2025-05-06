package com.equipo.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    // Relación con Colaboracion (basándonos en los campos 'emisor' y 'receptor' en Colaboracion)
    @OneToMany(mappedBy = "emisor")
    private List<Colaboracion> colaboracionesEmitidas;

    @OneToMany(mappedBy = "receptor")
    private List<Colaboracion> colaboracionesRecibidas;

    @OneToMany(mappedBy = "empleado")
    private List<Nomina> nominas;

    @ManyToMany
    @JoinTable(
            name = "empleado_etiqueta",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private List<Etiqueta> etiquetas;
}