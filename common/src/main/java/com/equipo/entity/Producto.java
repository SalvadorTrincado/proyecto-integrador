package com.equipo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data; // Se añade Lombok para simplificar
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "productos") // Cambiado de "Producto" a "productos" para seguir convenciones
@Data // Usar Lombok para getters, setters, toString, etc.
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false)
    private String descripcion; // [cite: 6, 31]

    @Column(nullable = false)
    private Double precio; // [cite: 6, 31]

    private String marca; // Opcional [cite: 6, 31]

    @Column(nullable = false)
    private Integer unidades; // [cite: 6, 33]

    @Column(nullable = false)
    private LocalDate fechaFabricacion; // [cite: 7, 33]

    @Column(nullable = false)
    private LocalDate fechaAlta; // Se establecerá al crear [cite: 25]

    @Column(nullable = false)
    private Integer valoracion = 0; // Valor por defecto 0 [cite: 26]

    private Boolean esPerecedero; // Opcional [cite: 33]

    // --- RELACIONES ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    @JsonBackReference
    private Proveedor proveedor;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    private String titulo;
    private String autor;
    private String editorial;
    private String tapa;
    private Integer numeroPaginas;
    private Boolean segundaMano;

    private Double dimensionAncho;
    private Double dimensionProfundo;
    private Double dimensionAlto;

    @ElementCollection
    @CollectionTable(name = "producto_colores", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "color")
    private List<String> colores = new ArrayList<>();

    private String talla;
    private String material;

    @PrePersist
    protected void onCreate() {
        this.fechaAlta = LocalDate.now(); // [cite: 25]
        if (this.valoracion == null) {
            this.valoracion = 0; // [cite: 26]
        }
    }
}