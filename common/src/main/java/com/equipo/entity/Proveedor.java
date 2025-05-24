package com.equipo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PROVEEDORES")
@Data
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Ejemplo de otros campos, ajustar según necesidad
    private String cif;
    private String direccion;
    private String telefono;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // Indica que este es el lado "padre" y debe ser serializado normalmente.
    private List<Producto> productos = new ArrayList<>();
}