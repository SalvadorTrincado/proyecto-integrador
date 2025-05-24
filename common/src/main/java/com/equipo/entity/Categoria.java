package com.equipo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "CATEGORIAS")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // O @JsonIgnore si no quieres serializar los productos desde la categoría
    private Set<Producto> productos = new HashSet<>();

    public Categoria() {}

    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}