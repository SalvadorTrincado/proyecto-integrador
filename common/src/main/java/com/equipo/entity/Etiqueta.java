package com.equipo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference; // IMPORTANTE
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "etiquetas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @ManyToMany(mappedBy = "etiquetas", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonBackReference // Lado "hijo", se omite para romper el ciclo de serialización
    private Set<Empleado> empleados = new HashSet<>();

    // Constructor adicional por si se necesita crear una etiqueta solo con el nombre
    public Etiqueta(String nombre) {
        this.nombre = nombre;
        this.empleados = new HashSet<>(); // Asegurar inicialización
    }
}