package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet; // Asegúrate de importar HashSet
import java.util.List;
import java.util.Set; // Asegúrate de importar Set
import java.util.UUID;

@Entity
@Table(name = "EMPLEADOS")
@Data
public class Empleado {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // La generación de ID se maneja manualmente o desde Usuario
    private UUID id;

    // PASO 1: Datos personales
    private String nombre;
    private String apellidos;
    private String fotografia;
    private String generoSeleccionado;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String paisNacimiento;
    @Column(length = 500)
    private String comentarios;

    // PASO 2: Datos de contacto
    private String tipoDocumento;
    private String documento;
    private String prefijoTelefono;
    private String telefonoMovil;
    private String tipoViaDireccionPpal;
    private String nombreViaDireccionPpal;
    private String numeroViaDireccionPpal;
    private String portalDireccionPpal;
    private String plantaDireccionPpal;
    private String puertaDireccionPpal;
    private String localidadDireccionPpal;
    private String regionDireccionPpal;
    private String codigoPostalDireccionPpal;

    // PASO 3: Datos profesionales
    private String departamento;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "empleado_especialidades", joinColumns = @JoinColumn(name = "empleado_id"))
    @Column(name = "especialidad")
    private List<String> especialidadesSeleccionadas = new ArrayList<>();

    // PASO 4: Datos económicos
    private String numeroCuenta;
    private String tipoContrato;
    private String categoriaProfesional;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String devengoPagaExtra;
    private LocalDate fechaIncorporacion;

    // NUEVA RELACIÓN CON ETIQUETA
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "empleado_etiqueta_join", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    @ToString.Exclude // Para evitar recursión en toString con Etiqueta
    @EqualsAndHashCode.Exclude // Para evitar recursión en equals/hashCode con Etiqueta
    private Set<Etiqueta> etiquetas = new HashSet<>();

    // Métodos helper para gestionar la relación bidireccional (opcional pero recomendado)
    public void addEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        etiqueta.getEmpleados().add(this);
    }

    public void removeEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
        etiqueta.getEmpleados().remove(this);
    }
}