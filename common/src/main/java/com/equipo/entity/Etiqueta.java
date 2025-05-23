package com.equipo.entity;

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
    @ToString.Exclude // Evitar recursión en toString con Empleado
    @EqualsAndHashCode.Exclude // Evitar recursión en equals/hashCode con Empleado
    @Builder.Default // Asegura que Lombok Builder respete la inicialización de la colección
    private Set<Empleado> empleados = new HashSet<>();

    // Constructor adicional por si se necesita crear una etiqueta solo con el nombre
    public Etiqueta(String nombre) {
        this.nombre = nombre;
    }
}