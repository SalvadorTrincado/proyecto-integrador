package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data; // Se añade Lombok para simplificar
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "productos") // Cambiado de "Producto" a "productos" para seguir convenciones
@Data // Usar Lombok para getters, setters, toString, etc.
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CAMPOS COMUNES REQUERIDOS POR EL PDF ---
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
    private Proveedor proveedor; // [cite: 18, 24]

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>(); // [cite: 6, 18, 33]


    // --- CAMPOS ESPECÍFICOS (EJEMPLOS) ---
    // Para tipo Libro
    private String titulo;
    private String autor;
    private String editorial;
    private String tapa; // Blanda, Dura
    private Integer numeroPaginas;
    private Boolean segundaMano;

    // Para tipo Mueble
    private Double dimensionAncho;
    private Double dimensionProfundo;
    private Double dimensionAlto;

    @ElementCollection // Para lista de colores
    @CollectionTable(name = "producto_colores", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "color")
    private List<String> colores = new ArrayList<>();

    // Para tipo Ropa (ejemplo)
    private String talla;
    private String material;


    // --- ATRIBUTOS ORIGINALES (revisar si se mantienen o se integran) ---
    // private String nombre; // El PDF usa "descripcion" como principal identificador textual. Si "nombre" es diferente, mantener. Si no, eliminar y usar "descripcion".
    // Por ahora, lo comento asumiendo que "descripcion" es el campo principal.
    // private Integer stock; // El PDF usa "unidades". Unificar a "unidades".

    // Constructores, Getters y Setters son generados por Lombok @Data
    // No es necesario definirlos explícitamente si se usa @Data.
    // Si no se usa @Data, se deben generar manualmente.

    @PrePersist
    protected void onCreate() {
        this.fechaAlta = LocalDate.now(); // [cite: 25]
        if (this.valoracion == null) {
            this.valoracion = 0; // [cite: 26]
        }
    }
}